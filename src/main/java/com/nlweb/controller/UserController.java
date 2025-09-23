package com.nlweb.controller;

import com.nlweb.dto.request.user.UpdateUserRequest;
import com.nlweb.dto.response.ApiResponse;
import com.nlweb.dto.response.PageResponse;
import com.nlweb.dto.UserDTO;
import com.nlweb.enums.UserSessionType;
import com.nlweb.enums.UserStatus;
import com.nlweb.service.UserService;
import com.nlweb.validation.groups.ValidationGroups;
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
import org.springframework.security.core.userdetails.UserDetails;
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

    /**
     * 내 정보 조회
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMyInfo(
            @AuthenticationPrincipal UserDetails principal) {

        String studentId = principal.getUsername();
        UserDTO userInfo = userService.getUserInfo(studentId);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 내 정보 수정
     */
    @Operation(summary = "내 정보 수정", description = "현재 사용자의 프로필 정보 수정")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyInfo(
            @AuthenticationPrincipal UserDetails principal,
            @Validated(ValidationGroups.ProfileUpdate.class) @RequestBody UpdateUserRequest request) {

        String studentId = principal.getUsername();
        UserDTO updatedUser = userService.updateUser(studentId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser, "프로필이 수정되었습니다"));
    }

    /**
     * 특정 사용자 정보 조회 (관리자 또는 본인만)
     */
    @Operation(summary = "사용자 정보 조회", description = "특정 사용자의 정보 조회 (권한 필요)")
    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or #studentId == authentication.name")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo(@PathVariable String studentId) {
        UserDTO userInfo = userService.getUserInfo(studentId);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 활성 사용자 목록 조회
     */
    @Operation(summary = "활성 사용자 목록", description = "활성화된 모든 사용자 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getActiveUsers() {
        List<UserDTO> users = userService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 세션별 사용자 조회
     */
    @Operation(summary = "세션별 사용자", description = "특정 세션(악기)의 사용자들 조회")
    @GetMapping("/session/{session}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersBySession(
            @PathVariable UserSessionType session) {

        List<UserDTO> users = userService.getUsersBySession(session);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    /**
     * 사용자 검색
     */
    @Operation(summary = "사용자 검색", description = "이름, 학번, 이메일로 사용자 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "ACTIVE") UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<UserDTO> users = userService.searchUsers(keyword, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.of(users)));
    }

    /**
     * 사용자 통계 조회 (관리자만)
     */
    @Operation(summary = "사용자 통계", description = "사용자 현황 통계 조회")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserService.UserStatistics>> getUserStatistics() {
        UserService.UserStatistics stats = userService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
