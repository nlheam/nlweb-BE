package com.nlweb.controller;

import com.nlweb.dto.request.admin.CreateAdminRequest;
import com.nlweb.dto.AdminDTO;
import com.nlweb.dto.response.ApiResponse;
import com.nlweb.dto.UserDTO;
import com.nlweb.service.AdminService;
import com.nlweb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 관리자 기능 REST API
 */
@Tag(name = "관리자 API", description = "관리자 전용 기능")
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    // ========== 사용자 승인 관리 ==========

    /**
     * 승인 대기 사용자 목록 조회
     */
    @Operation(summary = "승인 대기 사용자", description = "가입 승인을 기다리는 사용자 목록")
    @GetMapping("/users/pending")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getPendingUsers() {
        List<UserDTO> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.success(pendingUsers));
    }


    /**
     * 사용자 승인
     */
    @Operation(summary = "사용자 승인", description = "특정 사용자의 가입을 승인")
    @PostMapping("/users/{studentId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveUser(
            @PathVariable String studentId,
            @AuthenticationPrincipal UserDetails principal) {

        String approvedBy = principal.getUsername();
        userService.approveUser(studentId, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(null, "사용자 승인이 완료되었습니다"));
    }

    /**
     * 사용자 거절
     */
    @Operation(summary = "사용자 거절", description = "특정 사용자의 가입을 거절")
    @PostMapping("/users/{studentId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectUser(
            @PathVariable String studentId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails principal) {

        String rejectedBy = principal.getUsername();
        userService.rejectUser(studentId, rejectedBy, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "사용자 거절이 완료되었습니다"));
    }

    /**
     * 대량 승인 처리
     */
    @Operation(summary = "대량 사용자 승인", description = "여러 사용자를 한 번에 승인")
    @PostMapping("/users/approve-batch")
    public ResponseEntity<ApiResponse<Void>> approveUsersInBatch(
            @RequestBody List<String> studentIds,
            @AuthenticationPrincipal UserDetails principal) {

        String approvedBy = principal.getUsername();
        userService.approveUsersInBatch(studentIds, approvedBy);
        return ResponseEntity.ok(ApiResponse.success(null,
                String.format("%d명의 사용자 승인이 완료되었습니다", studentIds.size())));
    }

    /**
     * 사용자 비활성화
     */
    @Operation(summary = "사용자 비활성화", description = "특정 사용자 계정을 비활성화")
    @PostMapping("/users/{studentId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable String studentId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails principal) {

        String deactivatedBy = principal.getUsername();
        userService.deactivateUser(studentId, deactivatedBy, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "사용자가 비활성화되었습니다"));
    }

    // ========== 관리자 관리 ==========

    /**
     * 관리자 목록 조회
     */
    @Operation(summary = "관리자 목록", description = "모든 활성 관리자 목록 조회")
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<AdminDTO>>> getAdmins() {
        List<AdminDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.success(admins));
    }

    /**
     * 관리자 추가
     */
    @Operation(summary = "관리자 임명", description = "새로운 관리자 임명")
    @PostMapping("/admins")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<AdminDTO>> createAdmin(
            @Valid @RequestBody CreateAdminRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        String studentId = request.getStudentId();
        String role = request.getRole();
        String appointmentReason = request.getAppointmentReason();
        String appointedBy = request.getStudentId();
        AdminDTO admin = adminService.appointAdmin(studentId, role, appointmentReason, appointedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(admin, "관리자가 임명되었습니다"));
    }

    /**
     * 관리자 권한 수정
     */
    @Operation(summary = "관리자 권한 수정", description = "관리자의 역할 변경")
    @PutMapping("/admins/{studentId}/role")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<AdminDTO>> updateAdminRole(
            @PathVariable String studentId,
            @RequestParam String newRole,
            @AuthenticationPrincipal UserDetails principal) {

        String updatedBy = principal.getUsername();
        AdminDTO admin = adminService.updateAdminRole(studentId, newRole, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(admin, "관리자 권한이 수정되었습니다"));
    }

    /**
     * 관리자 비활성화
     */
    @Operation(summary = "관리자 해제", description = "관리자 권한 해제")
    @PostMapping("/admins/{studentId}/deactivate")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateAdmin(
            @PathVariable String studentId,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails principal) {

        String deactivatedBy = principal.getUsername();
        adminService.deleteAdmin(studentId, deactivatedBy, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "관리자 권한이 해제되었습니다"));
    }
}
