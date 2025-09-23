package com.nlweb.controller;

import com.nlweb.dto.UserDTO;
import com.nlweb.dto.request.auth.*;
import com.nlweb.dto.response.ApiResponse;
import com.nlweb.dto.response.auth.*;
import com.nlweb.dto.request.auth.RegisterRequest;
import com.nlweb.service.AuthService;
import com.nlweb.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @RequestBody @Valid RegisterRequest request,
            HttpServletRequest httpRequest)
    {
        UserDTO user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(user, "회원가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다."));
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "사용자 인증 및 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest)
    {
        LoginResponse response = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인에 성공했습니다."));
    }

    /**
     * 로그아웃
     */
    @Operation(summary = "로그아웃", description = "현재 토큰 무효화")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃 완료"));
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "기존 비밀번호 확인 후 새 비밀번호로 변경")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        String studentId = principal.getUsername();
        userService.changePassword(studentId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "비밀번호가 변경되었습니다"));
    }

    /**
     * 토큰 갱신
     */
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        TokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "토큰 갱신 완료"));
    }

    /**
     * 토큰 유효성 검증
     */
    @Operation(summary = "토큰 검증", description = "현재 토큰의 유효성 확인")
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @AuthenticationPrincipal UserDetails principal) {

        boolean isValid = principal != null;
        return ResponseEntity.ok(ApiResponse.success(isValid, "토큰 검증 완료"));
    }
}