package com.nlweb.dto.request.user;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    private String username;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "올바른 전화번호 형식이 아닙니다")
    private String phone;

    private String session; // VOCAL, GUITAR
}
