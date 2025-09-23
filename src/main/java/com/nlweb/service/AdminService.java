package com.nlweb.service;

import com.nlweb.dto.AdminDTO;
import com.nlweb.entity.Admin;
import com.nlweb.entity.User;
import com.nlweb.enums.UserStatus;
import com.nlweb.exception.user.UserNotFoundException;
import com.nlweb.repository.AdminRepository;
import com.nlweb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminCacheService adminCacheService;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    // ========== 관리자 조회 ==========

    public List<AdminDTO> getAllAdmins() {
        return adminCacheService.getAllAdmins().stream()
                .map(this::convertToAdminDTO)
                .toList();
    }

    public boolean isAdmin(String studentId) {
        return adminCacheService.isAdmin(studentId);
    }

    public boolean isAdmin(Long userId) {
        return adminCacheService.isAdmin(userId);
    }

    public Optional<AdminDTO> getAdmin(String studentId) {
        return adminCacheService.getAdmin(studentId).map(this::convertToAdminDTO);
    }

    public Optional<AdminDTO> getAdmin(Long userId) {
        return adminCacheService.getAdmin(userId).map(this::convertToAdminDTO);
    }

    // ========== 집부 임명 ==========

    @Transactional
    public AdminDTO appointAdmin(String studentId, String role, String appointmentReason, String appointedBy) {
        // 사용자 존재 및 상태 확인
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException(studentId));

        User appointer = userRepository.findByStudentId(appointedBy)
                .orElseThrow(() -> new UserNotFoundException(appointedBy));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("활성 상태의 사용자만 관리자로 임명할 수 있습니다");
        }

        // 이미 관리자인지 확인
        if (adminCacheService.isAdmin(user.getId())) {
            throw new IllegalArgumentException("이미 관리자로 등록된 사용자입니다: " + studentId);
        }

        // 임명자가 권한이 있는지 확인
        if (appointedBy != null && !adminCacheService.isAdmin(appointer.getAdmin().getId())) {
            throw new IllegalArgumentException("관리자 임명 권한이 없습니다: " + appointedBy);
        }

        // 관리자 생성
        Admin admin = Admin.builder()
                .user(user)
                .role(role)
                .appointmentReason(appointmentReason)
                .appointedBy(appointer.getAdmin())
                .user(user)
                .build();

        Admin savedAdmin = adminRepository.save(admin);

        adminCacheService.evictAllAdminsCache();

        log.info("새로운 관리자 임명: {} ({}) - 역할: {}, 임명자: {}, 임명 이유: {}",
                user.getUsername(), studentId, role, appointedBy, appointmentReason);

        return convertToAdminDTO(savedAdmin);
    }

    // ========== 관리자 관리 ==========

    @Transactional
    public AdminDTO updateAdminRole(String studentId, String newRole, String changedBy) {
        Admin admin = adminRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다: " + studentId));

        // 권한 확인
        if (!adminCacheService.isAdmin(changedBy)) {
            throw new IllegalArgumentException("관리자 역할 변경 권한이 없습니다");
        }

        String oldRole = admin.getRole();
        admin.updateRole(newRole);
        adminRepository.save(admin);

        adminCacheService.evictAdminCache(studentId);
        adminCacheService.evictAllAdminsCache();

        log.info("관리자 역할 변경: {} - {} -> {}, 변경자: {}",
                studentId, oldRole, newRole, changedBy);

        return convertToAdminDTO(admin);
    }

    @Transactional
    public AdminDTO deleteAdmin(String studentId, String removedBy, String reason) {

        Admin admin = adminRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다: " + studentId));

        if (!adminCacheService.isAdmin(removedBy)) {
            throw new IllegalArgumentException("관리자 해임 권한이 없습니다");
        }

        adminRepository.delete(admin);
        adminCacheService.evictAdminCache(studentId);
        adminCacheService.evictAllAdminsCache();

        log.info("집부 권한 해임: {} - {}, 해임자: {}, 해임 이유: {}", studentId, admin.getRole(), removedBy, reason);

        return convertToAdminDTO(admin);
    }

    public List<AdminDTO> searchAdmins(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAdmins();
        }
        return adminRepository.searchAdminsWithUser(keyword.trim()).stream()
                .map(this::convertToAdminDTO)
                .toList();
    }

    public AdminDTO convertToAdminDTO(Admin admin) {
        return AdminDTO.builder()
                .studentId(admin.getUser().getStudentId())
                .username(admin.getUser().getUsername())
                .batch(admin.getUser().getBatch())
                .role(admin.getRole())
                .appointmentReason(admin.getAppointmentReason())
                .appointedBy(admin.getAppointedBy().getStudentId())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }
}
