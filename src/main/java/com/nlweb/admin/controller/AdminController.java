package com.nlweb.admin.controller;

import com.nlweb.common.dto.*;
import com.nlweb.admin.dto.*;
import com.nlweb.user.dto.*;
import com.nlweb.admin.service.AdminService;
import com.nlweb.user.service.UserService;
import com.nlweb.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.*;
import jakarta.validation.*;
import java.util.List;

/** 관리자 기능 REST API */
@Tag(name = "관리자 API", description = "관리자 전용 기능")
@Slf4j
@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Validated
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @Operation(summary = "모든 관리자 조회", description = "시스템에 등록된 모든 관리자의 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdminInfo>>> getAllAdmins(
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<AdminInfo> admins = adminService.getAllAdmins(principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(admins));
    }

    @Operation(summary = "내 집부 역할 변경", description = "현재 로그인한 집부의 역할을 변경합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateAdminResponse>> updateAdminRole (
            @Valid @RequestBody UpdateAdminRequest updateAdminRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String studentId = principal.getUsername();
        UpdateAdminResponse response = adminService.updateAdmin(studentId, updateAdminRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "집부 권한 부여", description = "특정 사용자에게 집부 권한을 부여합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{studentId}")
    public ResponseEntity<ApiResponse<CreateAdminResponse>> appointAdmin(
            @PathVariable("studentId") @NotBlank @Size(max = 8) String studentId,
            @Valid @RequestBody CreateAdminRequest createAdminRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String appointedBy = principal.getUsername();
        CreateAdminResponse response = adminService.createAdmin(studentId, appointedBy, createAdminRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @Operation(summary = "집부 권한 제거", description = "특정 사용자로부터 집부 권한을 제거합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> revokeAdmin(
            @PathVariable("studentId") @NotBlank @Size(max = 8) String studentId,
            @Valid @RequestBody DeleteAdminRequest deleteAdminRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String removedBy = principal.getUsername();
        adminService.deleteAdmin(studentId, removedBy, deleteAdminRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "승인 대기 사용자 조회", description = "승인 대기 중인 사용자 목록을 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/pending")
    public ResponseEntity<ApiResponse<List<UserInfo>>> getPendingUsers() {
        List<UserInfo> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(ApiResponse.success(pendingUsers));
    }

    @Operation(summary = "사용자 상태 일괄 변경", description = "사용자의 상태를 변경합니다. (승인, 거절, 활성화, 비활성화, 금지)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/status")
    public ResponseEntity<ApiResponse<UpdateUserStatusResponse>> updateUserStatuses(
            @Valid @RequestBody UpdateUserStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String adminStudentId = principal.getUsername();
        UpdateUserStatusResponse response = adminService.updateUsersStatuses(adminStudentId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
