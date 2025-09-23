package com.nlweb.service;

import com.nlweb.config.properties.JwtProperties;
import com.nlweb.dto.UserDTO;
import com.nlweb.dto.request.auth.LoginRequest;
import com.nlweb.dto.request.auth.RefreshTokenRequest;
import com.nlweb.dto.response.auth.LoginResponse;
import com.nlweb.dto.response.auth.TokenResponse;
import com.nlweb.entity.User;
import com.nlweb.exception.user.UserNotFoundException;
import com.nlweb.exception.auth.InvalidCredentialsException;
import com.nlweb.repository.UserRepository;
import com.nlweb.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * 인증 서비스
 * JWT 토큰 기반 인증/인가 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    /**
     * 로그인 처리
     */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 1. 사용자 조회 (학번 또는 이메일)
        User user = userRepository.findByStudentIdOrEmail(request.getIdentifier())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        // 2. 계정 상태 확인
        validateUserAccount(user);

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("잘못된 비밀번호입니다");
        }

        // 4. 로그인 성공 처리
        handleSuccessfulLogin(user, httpRequest);

        // 5. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        // 6. Refresh Token을 Redis에 저장
        saveRefreshToken(user.getStudentId(), refreshToken);

        // 7. 응답 생성
        UserDTO userInfo = convertToUserDTO(user);

        // 집부면 관리자 정보 포함
        if (user.isAdmin()) {
            userInfo.setIsAdmin(true);
            userInfo.setAdminInfo(
                    UserDTO.AdminInfoDTO.builder()
                            .role(user.getAdmin().getRole())
                            .appointedDate(user.getAdmin().getCreatedAt())
                            .appointedBy(user.getAdmin().getAppointedBy().getStudentId())
                            .build()
            );
        }

        log.info("로그인 성공: {} ({})", user.getUsername(), user.getStudentId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userInfo(userInfo)
                .expiresIn(jwtProperties.getAccessTokenValidity() / 1000) // 초 단위
                .build();
    }

    /**
     * 토큰 갱신
     */
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("유효하지 않은 refresh token입니다");
        }

        // 2. 토큰에서 사용자 정보 추출
        String studentId = jwtTokenProvider.getStudentIdFromToken(refreshToken);

        // 3. Redis에서 저장된 Refresh Token과 비교
        String storedToken = getStoredRefreshToken(studentId);
        if (!refreshToken.equals(storedToken)) {
            throw new InvalidCredentialsException("refresh token이 일치하지 않습니다");
        }

        // 4. 사용자 조회 및 상태 확인
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        validateUserAccount(user);

        // 5. 새로운 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        // 6. 새로운 Refresh Token 저장
        saveRefreshToken(studentId, newRefreshToken);

        log.info("토큰 갱신 성공: {} ({})", user.getUsername(), studentId);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenValidity() / 1000)
                .build();
    }

    /**
     * 로그아웃 처리
     */
    @Transactional
    public void logout(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String studentId = jwtTokenProvider.getStudentIdFromToken(token);

            // Refresh Token 삭제
            deleteRefreshToken(studentId);

            // Access Token을 블랙리스트에 추가 (남은 유효시간만큼)
            long remainingTime = jwtTokenProvider.getRemainingTime(token);
            if (remainingTime > 0) {
                String blacklistKey = "auth:blacklist:" + jwtTokenProvider.getTokenId(token);
                redisTemplate.opsForValue().set(blacklistKey, "LOGGED_OUT",
                        Duration.ofMillis(remainingTime));
            }

            log.info("로그아웃 완료: {}", studentId);
        }
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean isTokenValid(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }

        // 블랙리스트 확인
        String tokenId = jwtTokenProvider.getTokenId(token);
        String blacklistKey = "auth:blacklist:" + tokenId;
        return !redisTemplate.hasKey(blacklistKey);
    }

    // ========== Private 메서드들 ==========

    private void validateUserAccount(User user) {

        // 계정 상태 확인
        if (!user.getStatus().isActive() && !user.getStatus().isInActive()) {
            throw new InvalidCredentialsException("비활성화된 계정입니다");
        }

        // 활성화 여부 확인
        if (!user.getStatus().isActive()) {
            throw new InvalidCredentialsException("비활성화된 계정입니다");
        }
    }

    private void handleSuccessfulLogin(User user, HttpServletRequest request) {
        // IP 주소 기록 (보안 감사용)
        String ipAddress = getClientIpAddress(request);
        log.info("로그인 성공 - 사용자: {}, IP: {}", user.getStudentId(), ipAddress);

        // 마지막 로그인 시간 갱신
        user.updateLastLogin();

        userRepository.save(user);
    }

    private void saveRefreshToken(String studentId, String refreshToken) {
        String key = "auth:refresh:" + studentId;
        Duration expiration = Duration.ofMillis(jwtProperties.getRefreshTokenValidity());
        redisTemplate.opsForValue().set(key, refreshToken, expiration);
    }

    private String getStoredRefreshToken(String studentId) {
        String key = "auth:refresh:" + studentId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    private void deleteRefreshToken(String studentId) {
        String key = "auth:refresh:" + studentId;
        redisTemplate.delete(key);
    }

    private UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .batch(user.getBatch())
                .session(user.getSession().getDescription())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus().getDescription())
                .lastLogin(user.getLastLogin())
                .isAdmin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
