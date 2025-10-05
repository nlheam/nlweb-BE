package com.nlweb.admin.dto;

import com.nlweb.admin.entity.Admin;
import java.time.LocalDateTime;
import lombok.*;

public class UpdateAdminResponse extends AdminInfo {

    UpdateAdminResponse(String studentId, String username, Integer batch, String phone, String role, String appointedBy, String appointmentReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(studentId, username, batch, phone, role, appointedBy, appointmentReason, createdAt, updatedAt);
    }

    public static UpdateAdminResponse fromEntity(Admin admin) {
        return new UpdateAdminResponse(
                admin.getUser().getStudentId(),
                admin.getUser().getUsername(),
                admin.getUser().getBatch(),
                admin.getRole(),
                admin.getAppointedBy(),
                admin.getAppointmentReason(),
                admin.getUser().getPhone(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );

    }
}
