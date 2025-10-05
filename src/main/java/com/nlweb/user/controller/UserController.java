package com.nlweb.user.controller;

import com.nlweb.common.enums.UserStatus;
import com.nlweb.user.dto.*;
import com.nlweb.common.dto.*;
import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.security.CustomUserDetails;
import com.nlweb.user.service.UserService;
import com.nlweb.common.validation.groups.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "사용자 API", description = "사용자 정보 조회/수정")
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    /** 활성 사용자 목록 조회 */
    @Operation(summary = "활성 사용자 목록", description = "활성화된 모든 사용자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserInfo>>> getActiveUsers(
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<UserInfo> response = userService.getActiveUsers(principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 사용자 생성 */
    @Operation(summary = "사용자 생성", description = "새로운 사용자 생성")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @Validated(ValidationGroups.Create.class) @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "사용자가 생성되었습니다"));
    }

    /** 내 정보 조회 */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal) {
        String studentId = principal.getUsername();
        UserInfo response = userService.getMyInfo(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 내 정보 수정 */
    @Operation(summary = "내 정보 수정", description = "현재 사용자의 프로필 정보 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Validated(ValidationGroups.ProfileUpdate.class) @RequestBody UpdateUserRequest request) {

        String studentId = principal.getUsername();
        UpdateUserResponse response = userService.updateUser(studentId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "프로필이 수정되었습니다"));
    }

    /** 내 정보 삭제 (소프트 삭제) */
    @Operation(summary = "내 정보 삭제", description = "현재 사용자의 계정을 삭제 (소프트 삭제)")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<DeleteUserResponse>> deleteMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Validated(ValidationGroups.Delete.class) @RequestBody DeleteUserRequest request) {

        String studentId = principal.getUsername();
        DeleteUserResponse response = userService.deleteUserSoft(studentId, studentId);
        return ResponseEntity.ok(ApiResponse.success(response, "계정이 삭제되었습니다. 삭제 6개월 뒤 복구가 불가능합니다."));
    }

    /** 내 정보 복구 */
    @Operation(summary = "내 정보 복구", description = "삭제된 계정을 복구합니다. (6개월 이내)")
    @PostMapping("/me/revive")
    public ResponseEntity<ApiResponse<UserInfo>> reviveMyInfo(
            @AuthenticationPrincipal CustomUserDetails principal) {

        String studentId = principal.getUsername();
        UserInfo response = userService.reviveUser(studentId);
        return ResponseEntity.ok(ApiResponse.success(response, "계정이 복구되었습니다."));
    }

    /** 특정 사용자 정보 조회 */
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보 조회")
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<UserInfo>> getUserInfo(
            @PathVariable String studentId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String requesterId = principal.getUsername();
        UserInfo response = userService.getUserInfo(studentId, principal.isAdmin() || studentId.equals(requesterId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 사용자 검색 */
    @Operation(summary = "사용자 검색", description = "이름, 학번, 이메일로 사용자 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserInfo>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Page<UserInfo> page = userService.searchUsers(keyword, status, pageable, principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(page)));
    }

    /** 세션별 사용자 조회 */
    @Operation(summary = "세션별 사용자", description = "특정 세션의 사용자들 조회")
    @GetMapping("/session/{session}")
    public ResponseEntity<ApiResponse<List<UserInfo>>> getUsersBySession(
            @PathVariable UserSessionType session,
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<UserInfo> users = userService.getUsersBySession(session, principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /** 기수별 사용자 조회 */
    @Operation(summary = "기수별 사용자", description = "특정 기수별 사용자들을 조회합니다.")
    @GetMapping("/batch/{batch}")
    public ResponseEntity<ApiResponse<List<UserInfo>>> getUsersByBatch(
            @PathVariable int batch,
            @AuthenticationPrincipal CustomUserDetails principal) {

        List<UserInfo> users = userService.getUsersByBatch(batch, principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /** 사용자 통계 조회 (관리자만) */
    @Operation(summary = "사용자 통계", description = "사용자 현황 통계 조회")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserStatistics>> getUserStatistics() {
        UserStatistics stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

}
