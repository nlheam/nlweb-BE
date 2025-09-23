package com.nlweb.dto.request.auth;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * 비밀번호 변경 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요")
    @Size(min = 4, max = 100, message = "비밀번호는 4자 이상 100자 이하여야 합니다")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요")
    private String confirmNewPassword;

    /**
     * 새 비밀번호 일치 확인
     */
    @AssertTrue(message = "새 비밀번호가 일치하지 않습니다")
    public boolean isNewPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}
