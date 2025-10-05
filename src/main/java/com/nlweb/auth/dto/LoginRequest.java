package com.nlweb.auth.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "LoginRequest", description = "로그인 요청 DTO")
public class LoginRequest {

    @Schema(description = "학번 또는 이메일", example = "25010001 or nlheam@nlheam.com")
    @NotBlank(message = "학번 또는 이메일을 입력해주세요")
    private String identifier; // 학번 또는 이메일

    @Schema(description = "비밀번호", example = "nlheam-password")
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하여야 합니다")
    private String password;
}
