package com.nlweb.admin.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
public class UpdateAdminRequest {

    @NotBlank(message = "집부 역할은 필수입니다")
    private String role;

}
