package com.nlweb.auth.controller;

import com.nlweb.user.dto.UserInfo;
import com.nlweb.auth.dto.*;
import com.nlweb.common.dto.*;
import com.nlweb.auth.service.AuthService;
import com.nlweb.user.service.UserService;
import com.nlweb.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰")
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    /** 회원가입 */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @RequestBody @Valid RegisterRequest registerRequest,
            HttpServletRequest httpRequest) {
        RegisterResponse registerResponse = authService.register(registerRequest, httpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(registerResponse));
    }

    /** 로그인 */
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest,
            HttpServletRequest httpRequest) {
        LoginResponse loginResponse = authService.login(loginRequest, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    /** 로그아웃 */
    @Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 비밀번호 변경 */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(principal.getUsername(), changePasswordRequest);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 토큰 재발급 */
    @Operation(summary = "토큰 재발급", description = "만료된 액세스 토큰을 리프레시 토큰으로 재발급합니다.")
    @PostMapping("/tokens")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        TokenResponse response = authService.refreshToken(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /** 토큰 유효성 검사 */
    @Operation(summary = "토큰 유효성 검사", description = "현재 액세스 토큰의 유효성을 검사합니다.")
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyToken(
            HttpServletRequest httpRequest) {
        boolean isValid = authService.isTokenValid(httpRequest);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }

}
