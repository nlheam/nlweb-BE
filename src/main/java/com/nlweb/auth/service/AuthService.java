package com.nlweb.auth.service;

import com.nlweb.config.properties.JwtProperties;
import com.nlweb.auth.dto.*;
import com.nlweb.user.entity.User;
import com.nlweb.user.dto.*;
import com.nlweb.user.repository.UserRepository;
import com.nlweb.user.service.*;
import com.nlweb.admin.dto.*;
import com.nlweb.common.exception.user.UserNotFoundException;
import com.nlweb.common.exception.auth.InvalidCredentialsException;
import com.nlweb.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserCacheService userCacheService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProperties jwtProperties;

    /** 회원가입 처리 */
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest httpRequest) {

        // 1. 중복 검사
        validateUserRegistration(registerRequest);

        // 2. IP 추출
        String ipAddress = getClientIpAddress(httpRequest);

        // 3. 사용자 생성
        CreateUserResponse response = userService.createUser(CreateUserRequest.from(registerRequest));

        log.info("회원가입 성공 - 학번: {}, IP: {}", registerRequest.getStudentId(), ipAddress);

        User user = userCacheService.getUserByStudentId(response.getStudentId())
                .orElseThrow(() -> new UserNotFoundException(response.getStudentId()));

        return RegisterResponse.fromEntity(user);
    }

    /** 로그인 처리 */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userCacheService.getUserByStudentId(request.getIdentifier())
                .or(() -> userCacheService.getUserByEmail(request.getIdentifier()))
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        validateUserAccount(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("잘못된 비밀번호입니다");
        }

        handleSuccessfulLogin(user, httpRequest);

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        saveRefreshToken(user.getStudentId(), refreshToken);

        UserInfo userInfo = UserInfo.fromEntity(user, true);

        log.info("로그인 성공: {} ({})", user.getUsername(), user.getStudentId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userInfo(userInfo)
                .expiresIn(jwtProperties.getAccessTokenValidity() / 1000)
                .refreshExpiresIn(jwtProperties.getRefreshTokenValidity() / 1000)
                .build();
    }

    /** 로그아웃 처리 */
    @Transactional
    public void logout(HttpServletRequest httpRequest) {
        String token = jwtTokenProvider.resolveToken(httpRequest);
        String ipAddress = getClientIpAddress(httpRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String studentId = jwtTokenProvider.getStudentIdFromToken(token);
            deleteRefreshToken(studentId);

            long remainingTime = jwtTokenProvider.getRemainingTime(token);
            if (remainingTime > 0) {
                String blacklistKey = "auth:blacklist:" + jwtTokenProvider.getTokenId(token);
                redisTemplate.opsForValue().set(blacklistKey, "LOGGED_OUT", Duration.ofMillis(remainingTime));
            }

            log.info("로그아웃 완료: {}", studentId);
        } else {
            log.warn("유효하지 않은 토큰으로 로그아웃 시도: IP: {}", ipAddress);
        }
    }

    public void changePassword(String studentId, ChangePasswordRequest request) {
        userService.changePassword(studentId, ChangePasswordRequestInternal.fromInternalRequest(request));
    }

    /** 토큰 갱신 */
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new InvalidCredentialsException("유효하지 않은 리프레시 토큰입니다");
        }

        // 토큰에서 학번 추출
        String studentId = jwtTokenProvider.getStudentIdFromToken(refreshToken);

        // Redis에 저장된 Refresh Token 조회
        String storedToken = getStoredRefreshToken(studentId);
        if (!storedToken.equals(refreshToken)) {
            throw new InvalidCredentialsException("리프레시 토큰이 일치하지 않습니다");
        }

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        validateUserAccount(user);

        String newAccessToken = jwtTokenProvider.createAccessToken(user);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

        saveRefreshToken(studentId, newRefreshToken);

        log.info("토큰 갱신 성공: {} ({})", user.getUsername(), studentId);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenValidity() / 1000)
                .build();
    }

    /** 토큰 유효성 검사 */
    public boolean isTokenValid(HttpServletRequest httpRequest) {
        String token = jwtTokenProvider.resolveToken(httpRequest);
        if (!jwtTokenProvider.validateToken(token)) {
            return false;
        }

        String tokenId = jwtTokenProvider.getTokenId(token);
        String blacklistKey = "auth:blacklist:" + tokenId;
        return !redisTemplate.hasKey(blacklistKey);
    }

    // ===================== Private 메소드 ===================== //

    private void validateUserRegistration(RegisterRequest request) {
        if (userCacheService.isUserExistsByStudentId(request.getStudentId())) {
            throw new InvalidCredentialsException("이미 등록된 학번입니다");
        }

        if (userCacheService.isUserExistsByEmail(request.getEmail())) {
            throw new InvalidCredentialsException("이미 등록된 이메일입니다");
        }

        if (userCacheService.isUserExistsByPhone(request.getPhone())) {
            throw new InvalidCredentialsException("이미 등록된 전화번호입니다");
        }
    }

    private void validateUserAccount(User user) {

        if (user.getStatus().isPending()) {
            throw new InvalidCredentialsException("승인 대기 중인 계정입니다");
        }

        if (user.getStatus().isRejected()) {
            throw new InvalidCredentialsException("가입이 거부된 계정입니다");
        }

        if (user.getStatus().isSuspended()) {
            throw new InvalidCredentialsException("정지된 계정입니다");
        }
    }

    private void handleSuccessfulLogin(User user, HttpServletRequest request) {
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
