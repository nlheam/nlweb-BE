package com.nlweb.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RegisterRequest", description = "회원가입 요청 DTO")
public class RegisterRequest {

    @Schema(description = ("학번 (8자리 숫자)"), example = "25010001")
    @NotBlank(message = "학번은 필수입니다")
    @Pattern(regexp = "^[0-9]{8}$", message = "학번은 8자리 숫자여야 합니다")
    private String studentId;

    @Schema(description = "이름", example = "홍길동")
    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    private String username;

    @Schema(description = "비밀번호 (4자 이상 100자 이하)", example = "nlheam-password")
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하여야 합니다")
    private String password;

    @Schema(description = "비밀번호 확인", example = "nlheam-password")
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;

    @Schema(description = "이메일", example = "nlheam@nlheam.com")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Schema(description = "전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "올바른 전화번호 형식이 아닙니다")
    private String phone;

    @Schema(description = "기수 (1 이상 100 이하)", example = "38")
    @NotNull(message = "기수는 필수입니다")
    @Min(value = 1, message = "기수는 1 이상이어야 합니다")
    @Max(value = 100, message = "기수는 100 이하여야 합니다")
    private Integer batch;

    @Schema(description = "세션 (VOCAL, LEAD_GUITAR, etc.)", example = "VOCAL")
    @NotBlank(message = "세션을 선택해주세요")
    private String session; // VOCAL, LEAD_GUITAR, etc.

    @AssertTrue(message = "비밀번호가 일치하지 않습니다")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
