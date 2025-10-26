package com.nlweb.admin.dto;

import com.nlweb.common.validation.groups.ValidationGroups;
import lombok.*;
import jakarta.validation.constraints.*;

@Data
@Builder
public class CreateAdminRequest {


    @NotBlank(message = "집부 역할은 필수입니다", groups = ValidationGroups.Create.class)
    @Size(max = 30, message = "집부 역할은 최대 30자입니다.")
    private String role;

    @NotBlank(message = "임명 사유는 필수입니다", groups = ValidationGroups.Create.class)
    @Size(max = 500, message = "임명 사유는 최대 500자입니다.")
    private String appointmentReason;

}

