package com.nlweb.user.service;

import com.nlweb.admin.service.AdminCacheService;
import com.nlweb.user.dto.*;
import com.nlweb.user.entity.User;
import com.nlweb.user.repository.UserRepository;
import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.enums.UserStatus;
import com.nlweb.common.exception.user.UserNotFoundException;
import com.nlweb.common.exception.user.DuplicateUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserCacheService userCacheService;
    private final UserRepository userRepository;
    private final AdminCacheService adminCacheService;
    private final PasswordEncoder passwordEncoder;

    /** 내 정보 조회 */
    public UserInfo getMyInfo(String studentId) {
        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        return UserInfo.fromEntity(user, true);
    }

    /** 학번으로 사용자 조회 */
    public UserInfo getUserInfo(String studentId, Boolean includePrivateInfo) {
        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        return UserInfo.fromEntity(user, includePrivateInfo);
    }

    /** 활성 사용자 목록 조회 */
    public List<UserInfo> getActiveUsers(Boolean includePrivateInfo) {
        return userCacheService.getAllActiveUsers()
                .stream()
                .map(user -> UserInfo.fromEntity(user, includePrivateInfo))
                .collect(Collectors.toList());
    }

    /** 승인 대기 중인 사용자 목록 조회 */
    public List<UserInfo> getPendingUsers() {
        return userCacheService.getAllPendingUsers()
                .stream()
                .map(user -> UserInfo.fromEntity(user, true))
                .collect(Collectors.toList());
    }

    /** 세션별 사용자 조회 */
    public List<UserInfo> getUsersBySession(UserSessionType session, Boolean includePrivateInfo) {
        return userCacheService.getUsersBySession(session)
                .stream()
                .map(user -> UserInfo.fromEntity(user, includePrivateInfo))
                .collect(Collectors.toList());
    }

    /** 기수별 사용자 조회 */
    public List<UserInfo> getUsersByBatch(int batch, Boolean includePrivateInfo) {
        return userCacheService.getUsersByBatch(batch)
                .stream()
                .map(user -> UserInfo.fromEntity(user, includePrivateInfo))
                .collect(Collectors.toList());
    }

    /** 사용자 검색 */
    public Page<UserInfo> searchUsers(String keyword, UserStatus status, Pageable pageable, Boolean includePrivateInfo) {
        return userRepository.searchUsers(keyword, status, pageable)
                .map(user -> UserInfo.fromEntity(user, includePrivateInfo));
    }

    /** 사용자 생성 */
    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        // 학생 ID 중복 체크
        if (userRepository.existsByStudentId(request.getStudentId())) {
            throw new DuplicateUserException("Student ID already exists: " + request.getStudentId());
        }

        // 이메일 중복 체크
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateUserException("Email already exists: " + request.getEmail());
        }

        // 전화번호 중복 체크
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone().trim())) {
            throw new DuplicateUserException("Phone number already exists: " + request.getPhone());
        }

        // 사용자 엔티티 생성
        User user = User.builder()
                .studentId(request.getStudentId())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null)
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .batch(request.getBatch())
                .session(request.getSession())
//                .status(UserStatus.PENDING)
                .status(UserStatus.ACTIVE) // 개발 중엔 바로 승인
                .build();

        // 사용자 저장
        User savedUser = userRepository.save(user);
        log.info("새로운 사용자 생성 완료: {}", savedUser.getStudentId());

        return CreateUserResponse.from(savedUser);
    }

    /** 사용자 정보 수정 */
    @Transactional
    public UpdateUserResponse updateUser(String studentId, UpdateUserRequest request) {
        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userCacheService.isUserExistsByEmail(request.getEmail())) {
                throw new DuplicateUserException("이미 사용 중인 이메일입니다.");
            }

            user.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
            if (userCacheService.isUserExistsByPhone(request.getPhone())) {
                throw new DuplicateUserException("이미 사용 중인 전화번호입니다.");
            }

            user.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);
        userCacheService.evictAllActiveUsersCache();

        log.info("사용자 정보 수정: {} ({})", user.getUsername(), studentId);

        return UpdateUserResponse.from(updatedUser);
    }

    /** 비밀번호 변경 */
    @Transactional
    public void changePassword(String studentId, ChangePasswordRequestInternal request) {

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);

        log.info("사용자 비밀번호 변경: {} ({})", user.getUsername(), studentId);
    }

    /** 사용자 보컬 허용 상태 변경 */
    @Transactional
    public void setUserVocalable(String studentId, Boolean isVocalable, String modifiedBy) {
        assertAdmin(modifiedBy);

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (user.getSession().equals(UserSessionType.VOCAL)) {
            throw new IllegalStateException("보컬 세션 사용자는 보걸 세션 허용 설정을 변경할 수 없습니다: " + studentId);
        }

        user.setIsVocalable(isVocalable);
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);
        userCacheService.evictAllActiveUsersCache();

        log.info("사용자 보걸 세션 허용 설정 변경: {} ({}) to {}", user.getUsername(), studentId, isVocalable);
    }

    /** 사용자 소프트 삭제 (복구 가능) */
    @Transactional
    public DeleteUserResponse deleteUserSoft(String studentId, String deletedBy) {
        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);

        log.info("사용자 소프트 삭제: {} ({}) by {}", user.getUsername(), studentId, deletedBy);

        return DeleteUserResponse.from(user, "사용자가 소프트 삭제되었습니다.");
    }

    /** 사용자 복구 */
    @Transactional
    public UserInfo reviveUser(String studentId) {
        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        if (user.getStatus() != UserStatus.DELETED) {
            throw new IllegalStateException("소프트 삭제된 사용자만 복구할 수 있습니다: " + studentId);
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);
        userCacheService.evictAllActiveUsersCache();

        log.info("사용자 복구: {} ({})", user.getUsername(), studentId);

        return UserInfo.fromEntity(user, true);
    }

    /** 사용자 영구 삭제 */
    @Transactional
    public int deleteUserHard() {

        List<User> softDeletedUsers = userRepository.findByStatus(UserStatus.DELETED);
        int count = 0;

        for (User user : softDeletedUsers) {
            if (user.canBeHardDeleted()) {
                userRepository.delete(user);
                count++;
                log.info("사용자 영구 삭제: {} ({})", user.getUsername(), user.getStudentId());
            }
        }

        return count;
    }

    /** 사용자 가입 승인 */
    @Transactional
    public void approveUser(String studentId, String approvedBy) {
        assertAdmin(approvedBy);

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);
        userCacheService.evictAllActiveUsersCache();
        userCacheService.evictAllPendingUsersCache();

        log.info("사용자 승인: {} ({}) by {}", user.getUsername(), studentId, approvedBy);
    }

    /** 사용자 가입 거절 */
    @Transactional
    public void rejectUser(String studentId, String rejectedBy) {
        assertAdmin(rejectedBy);

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        userCacheService.evictUserCacheByStudentId(studentId);
        userCacheService.evictAllPendingUsersCache();

        log.info("사용자 거부: {} ({}) by {}", user.getUsername(), studentId, rejectedBy);
    }

    /** 사용자 통계 정보 조회 */
    @Transactional
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        long pendingUsers = userRepository.countByStatus(UserStatus.PENDING);

        List<Object[]> sessionStats = userRepository.countBySessionAndStatus(UserStatus.ACTIVE);
        List<Object[]> batchStats = userRepository.countByBatchAndStatus(UserStatus.ACTIVE);

        return UserStatistics.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .pendingUsers(pendingUsers)
                .sessionStatistics(sessionStats)
                .batchStatistics(batchStats)
                .build();
    }

    /** 관리자 권한 확인 */
    private void assertAdmin(String studentId) {
        if (studentId == null || !adminCacheService.isAdmin(studentId)) {
            throw new SecurityException("관리자 권한이 필요합니다: " + studentId);
        }
    }

}
