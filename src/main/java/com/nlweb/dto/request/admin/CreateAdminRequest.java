package com.nlweb.dto.request.admin;

import com.nlweb.validation.annotation.StudentId;
import com.nlweb.validation.groups.ValidationGroups;
import lombok.*;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAdminRequest {

    @NotBlank(message = "학번은 필수입니다", groups = ValidationGroups.Create.class)
    @StudentId(groups = ValidationGroups.Create.class)
    private String studentId;

    @NotBlank(message = "관리자 역할은 필수입니다", groups = ValidationGroups.Create.class)
    private String role;

    @NotBlank(message = "임명 사유는 필수입니다", groups = ValidationGroups.Create.class)
    @Size(max = 500, message = "임명 사유는 최대 500")
    private String appointmentReason;

    @NotBlank(message = "임명자는 필수입니다", groups = ValidationGroups.Create.class)
    @StudentId(groups = ValidationGroups.Create.class)
    private String appointedBy;
}
