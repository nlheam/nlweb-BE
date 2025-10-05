package com.nlweb.admin.dto;

import com.nlweb.admin.entity.Admin;
import lombok.*;

@Data
@Builder
public class CreateAdminResponse {

    private Long adminId;
    private String studentId;
    private String role;
    private String appointedBy;
    private String appointmentReason;

    public static CreateAdminResponse fromEntity(Admin admin) {
        return CreateAdminResponse.builder()
                .adminId(admin.getId())
                .studentId(admin.getUser().getStudentId())
                .role(admin.getRole())
                .appointedBy(admin.getAppointedBy())
                .appointmentReason(admin.getAppointmentReason())
                .build();
    }

}
