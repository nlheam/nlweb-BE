package com.nlweb.admin.service;

import com.nlweb.admin.dto.*;
import com.nlweb.admin.entity.Admin;
import com.nlweb.user.entity.User;
import com.nlweb.user.dto.UserInfo;
import com.nlweb.common.enums.UserStatus;
import com.nlweb.common.exception.user.UserNotFoundException;
import com.nlweb.admin.repository.AdminRepository;
import com.nlweb.user.repository.UserRepository;
import com.nlweb.user.service.UserCacheService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminCacheService adminCacheService;
    private final UserCacheService userCacheService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    /** 모든 관리자 조회 */
    public List<AdminInfo> getAllAdmins(Boolean includePrivateInfo) {
        return adminCacheService.getAllAdmins()
                .stream()
                .map(admin -> AdminInfo.fromEntity(admin, includePrivateInfo))
                .toList();
    }

    /** 학생 ID로 관리자 여부 확인 */
    public boolean isAdminByStudentId(String studentId) {
        return adminCacheService.isAdmin(studentId);
    }

    /** 사용자 ID로 관리자 여부 확인 */
    public boolean isAdminByUserId(Long userId) {
        return adminCacheService.isAdmin(userId);
    }

    /** 학생 ID로 관리자 조회 */
    public Optional<AdminInfo> getAdminByStudentId(String studentId, boolean includePrivateInfo) {
        return adminCacheService.getAdmin(studentId).map(admin -> AdminInfo.fromEntity(admin, includePrivateInfo));
    }

    /** 사용자 ID로 관리자 조회 */
    public Optional<AdminInfo> getAdminByUserId(Long userId, boolean includePrivateInfo) {
        return adminCacheService.getAdmin(userId).map(admin -> AdminInfo.fromEntity(admin, includePrivateInfo));
    }

    /** 관리자 생성 */
    @Transactional
    public CreateAdminResponse createAdmin(String studentId, String appointedBy, CreateAdminRequest createAdminRequest) {

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + studentId));

        User appointer = null;

        if (!Objects.equals(appointedBy, "SYSTEM")) {
            appointer = userCacheService.getUserByStudentId(appointedBy)
                    .orElseThrow(() -> new UserNotFoundException("임명자를 찾을 수 없습니다: " + appointedBy));
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("비활성화된 사용자는 관리자로 임명할 수 없습니다: " + user.getStudentId());
        }

        if (adminCacheService.isAdmin(studentId)) {
            throw new IllegalArgumentException("이미 관리자로 등록된 사용자입니다: " + user.getStudentId());
        }

        if (appointedBy != null && !appointedBy.equals("SYSTEM") && appointer == null) {
            throw new IllegalArgumentException("임명자 정보가 올바르지 않습니다: " + appointedBy);
        }

        Admin admin = Admin.builder()
                .user(user)
                .role(createAdminRequest.getRole())
                .appointedBy(appointer != null ? appointer.getStudentId() : "SYSTEM")
                .appointmentReason(createAdminRequest.getAppointmentReason())
                .build();

        Admin savedAdmin = adminRepository.save(admin);

        adminCacheService.evictAllAdminsCache();

        log.info("새로운 관리자 임명: {} ({}) - 역할: {}, 임명자: {}, 임명 이유: {}",
                user.getUsername(), user.getStudentId(), savedAdmin.getRole(),
                appointedBy, createAdminRequest.getAppointmentReason());

        return CreateAdminResponse.fromEntity(savedAdmin);
    }

    /** 관리자 수정 */
    @Transactional
    public UpdateAdminResponse updateAdmin(String studentId, UpdateAdminRequest updateAdminRequest) {
        Admin admin = adminCacheService.getAdmin(studentId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다: " + studentId));

        admin.setRole(updateAdminRequest.getRole());

        Admin updatedAdmin = adminRepository.save(admin);

        adminCacheService.evictAdminCache(studentId);
        adminCacheService.evictAllAdminsCache();

        log.info("관리자 정보 업데이트: {} - 새로운 역할: {}", updatedAdmin.getUser().getStudentId(), updatedAdmin.getRole());

        return UpdateAdminResponse.fromEntity(updatedAdmin);
    }

    /** 관리자 삭제 */
    @Transactional
    public void deleteAdmin(String studentId, String removedBy, DeleteAdminRequest deleteAdminRequest) {
        Admin admin = adminCacheService.getAdmin(studentId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다: " + studentId));

        adminRepository.delete(admin);

        adminCacheService.evictAdminCache(studentId);
        adminCacheService.evictAllAdminsCache();

        log.info("관리자 삭제: {} - 삭제자: {}, 이유: {}",
                studentId,
                removedBy,
                deleteAdminRequest.getReason());
    }

    /** 사용자 상태 업데이트 (승인, 거부, 활성화, 비활성화, 금지) */
    @Transactional
    public UpdateUserStatusResponse updateUsersStatuses(String studentId, UpdateUserStatusRequest request) {
        List<User> users = userCacheService.getUsersByStudentIds(request.getStudentIds());
        List<UserInfo> successedUsers = new ArrayList<>();
        List<UpdateUserStatusResponse.FailedInfo> errors = new ArrayList<>();

        Set<String> foundStudentIds = users.stream()
                .map(User::getStudentId)
                .collect(Collectors.toSet());

        request.getStudentIds().stream()
                .filter(id -> !foundStudentIds.contains(id))
                .forEach(id -> errors.add(new UpdateUserStatusResponse.FailedInfo(
                        id, "사용자를 찾을 수 없습니다: " + id)));

        for (User user : users) {
            try {
                validateStatusTransition(user, request.getAction());
                updateUserStatusInternal(user, request.getAction(), request.getReason());
                successedUsers.add(UserInfo.fromEntity(user, true));
            } catch (Exception e) {
                errors.add(new UpdateUserStatusResponse.FailedInfo(user.getStudentId(), e.getMessage()));
            }
        }

        if (!successedUsers.isEmpty()) {
            userRepository.saveAll(users.stream()
                    .filter(user -> successedUsers.stream()
                    .anyMatch(successUser -> successUser.getStudentId().equals(user.getStudentId())))
                    .toList());
        }

        userCacheService.evictAllActiveUsersCache();
        userCacheService.evictAllPendingUsersCache();

        return UpdateUserStatusResponse.builder()
                .successUsers(successedUsers)
                .errors(errors)
                .totalCount(request.getStudentIds().size())
                .successCount(successedUsers.size())
                .errorCount(errors.size())
                .build();
    }

    // ========================== Private Methods ==========================

    private void validateStatusTransition(User user, String action) {
        switch (action) {
            case "approve" -> {
                if (user.getStatus() != UserStatus.PENDING) {
                    throw new IllegalStateException("승인 대기 중인 사용자만 승인할 수 있습니다: " + user.getStudentId());
                }
            }
            case "reject" -> {
                if (user.getStatus() != UserStatus.PENDING) {
                    throw new IllegalStateException("승인 대기 중인 사용자만 거부할 수 있습니다: " + user.getStudentId());
                }
            }
            case "activate" -> {
                if (user.getStatus() != UserStatus.INACTIVE && user.getStatus() != UserStatus.SUSPENDED) {
                    throw new IllegalStateException("비활성화 또는 정지된 사용자만 활성화할 수 있습니다: " + user.getStudentId());
                }
            }
            case "deactivate" -> {
                if (user.getStatus() != UserStatus.ACTIVE) {
                    throw new IllegalStateException("활성화된 사용자만 비활성화할 수 있습니다: " + user.getStudentId());
                }
            }
            case "suspend" -> {
                if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.INACTIVE) {
                    throw new IllegalStateException("활성화된 사용자만 정지할 수 있습니다: " + user.getStudentId());
                }
            }
            default -> throw new IllegalArgumentException("알 수 없는 액션입니다: " + action);
        }
    }

    private void updateUserStatusInternal(User user, String action, String reason) {
        switch (action) {
            case "approve", "activate" -> user.setStatus(UserStatus.ACTIVE);
            case "reject" -> user.setStatus(UserStatus.REJECTED);
            case "deactivate" -> user.setStatus(UserStatus.INACTIVE);
            case "suspend" -> user.setStatus(UserStatus.SUSPENDED);
            default -> throw new IllegalArgumentException("알 수 없는 액션입니다: " + action);
        }

        userRepository.save(user);
        log.info("사용자 상태 변경: {} - 새로운 상태: {}, 이유: {}", user.getStudentId(), user.getStatus(), reason);
    }

}
