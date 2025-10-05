package com.nlweb.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UpdateUserRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;

}
