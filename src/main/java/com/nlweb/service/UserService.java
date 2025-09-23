package com.nlweb.service;

import com.nlweb.dto.request.auth.RegisterRequest;
import com.nlweb.dto.request.user.UpdateUserRequest;
import com.nlweb.dto.UserDTO;
import com.nlweb.entity.User;
import com.nlweb.enums.UserSessionType;
import com.nlweb.enums.UserStatus;
import com.nlweb.exception.user.UserNotFoundException;
import com.nlweb.exception.user.DuplicateUserException;
import com.nlweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AdminCacheService adminCacheService;
    private final PasswordEncoder passwordEncoder;

    // ========== 조회 메서드 ==========

    /**
     * 학번으로 사용자 조회
     */
    @Cacheable(value = "user", key = "#studentId")
    public UserDTO getUserInfo(String studentId) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        return convertToUserDTO(user);
    }

    /**
     * 활성 사용자 목록 조회
     */
    public List<UserDTO> getActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE)
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * 승인 대기 중인 사용자 목록 조회
     */
    public List<UserDTO> getPendingUsers() {
        return userRepository.findByStatusOrderByCreatedAtAsc(UserStatus.PENDING)
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * 세션별 사용자 조회
     */
    public List<UserDTO> getUsersBySession(UserSessionType session) {
        return userRepository.findBySessionAndStatus(session, UserStatus.ACTIVE)
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 검색
     */
    public Page<UserDTO> searchUsers(String keyword, UserStatus status, Pageable pageable) {
        return userRepository.searchUsers(keyword, status, pageable)
                .map(this::convertToUserDTO);
    }

    // ========== 회원가입 및 사용자 관리 ==========

    /**
     * 회원가입
     */
    @Transactional
    public UserDTO registerUser(RegisterRequest request) {
        // 중복 검사
        validateUserRegistration(request);

        // 사용자 생성
        User user = User.builder()
                .studentId(request.getStudentId())
                .username(request.getUsername())
                .batch(request.getBatch())
                .password(passwordEncoder.encode(request.getPassword()))
                .session(UserSessionType.fromString(request.getSession()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .status(UserStatus.PENDING) // 관리자 승인 필요
                .build();

        User savedUser = userRepository.save(user);

        log.info("신규 사용자 등록: {} ({})", user.getUsername(), user.getStudentId());

        return convertToUserDTO(savedUser);
    }

    /**
     * 사용자 정보 수정
     */
    @Transactional
    @CacheEvict(value = "user", key = "#studentId")
    public UserDTO updateUser(String studentId, UpdateUserRequest request) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        // 이메일 변경시 중복 확인
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateUserException("이미 사용 중인 이메일입니다");
            }
            user.setEmail(request.getEmail());
        }

        // 전화번호 변경시 중복 확인
        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(request.getPhone())) {
                throw new DuplicateUserException("이미 사용 중인 전화번호입니다");
            }
            user.setPhone(request.getPhone());
        }

        // 세션 변경
        if (request.getSession() != null) {
            user.setSession(UserSessionType.fromString(request.getSession()));
        }

        User updatedUser = userRepository.save(user);

        log.info("사용자 정보 수정: {} ({})", user.getUsername(), studentId);

        return convertToUserDTO(updatedUser);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String studentId, String oldPassword, String newPassword) {
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        // 기존 비밀번호 확인
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다");
        }

        // 새 비밀번호 설정
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("비밀번호 변경: {}", studentId);
    }

    // ========== 관리자 기능 ==========

    /**
     * 사용자 승인
     */
    @Transactional
    @CacheEvict(value = "user", key = "#studentId")
    public void approveUser(String studentId, String approvedBy) {

        assertAdmin(approvedBy);
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        log.info("사용자 승인: {} by {}", studentId, approvedBy);
    }

    @Transactional
    public void approveUsersInBatch(List<String> studentIds, String approvedBy) {

        assertAdmin(approvedBy);
        List<User> users = new ArrayList<>();
        for (String studentId : studentIds) {
            userRepository.findByStudentId(studentId).ifPresent(users::add);
        }

        for (User user : users) {
            if (user.getStatus().isPending()) {
                user.setStatus(UserStatus.ACTIVE);
                log.info("사용자 승인: {} by {}", user.getStudentId(), approvedBy);
            }
        }

        userRepository.saveAll(users);
    }

    /**
     * 사용자 거절
     */
    @Transactional
    @CacheEvict(value = "user", key = "#studentId")
    public void rejectUser(String studentId, String rejectedBy, String reason) {

        assertAdmin(rejectedBy);
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태가 아닙니다");
        }

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        log.info("사용자 거절: {} by {} (사유: {})", studentId, rejectedBy, reason);
    }

    /**
     * 사용자 비활성화
     */
    @Transactional
    @CacheEvict(value = "user", key = "#studentId")
    public void deactivateUser(String studentId, String deactivatedBy, String reason) {

        assertAdmin(deactivatedBy);
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);

        log.info("사용자 비활성화: {} by {} (사유: {})", studentId, deactivatedBy, reason);
    }

    // ========== 통계 및 분석 ==========

    /**
     * 사용자 통계 조회
     */
    public UserStatistics getUserStatistics() {
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers();
        Long pendingUsers = userRepository.countByStatus(UserStatus.PENDING);

        List<Object[]> sessionStats = userRepository.countBySessionAndStatus(UserStatus.ACTIVE);

        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .pendingUsers(pendingUsers)
                .sessionStatistics(sessionStats)
                .build();
    }

    // ========== Private 메서드들 ==========

    private void assertAdmin(String studentId) {
        if (studentId == null || !adminCacheService.isAdmin(studentId)) {
            throw new SecurityException("관리자 권한이 필요합니다: " + studentId);
        }
    }

    private void validateUserRegistration(RegisterRequest request) {
        // 학번 중복 확인
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new DuplicateUserException("이미 등록된 학번입니다");
        }

        // 이메일 중복 확인
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException("이미 사용 중인 이메일입니다");
        }

        // 전화번호 중복 확인
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateUserException("이미 사용 중인 전화번호입니다");
        }
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
                .isAdmin(user.isAdmin())
                .isActive(user.getStatus().isActive())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    // 통계 DTO
    @lombok.Data
    @lombok.Builder
    public static class UserStatistics {
        private Long totalUsers;
        private Long activeUsers;
        private Long pendingUsers;
        private List<Object[]> sessionStatistics;
    }
}
