package com.nlweb.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import com.nlweb.admin.entity.Admin;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdminInfo {

    private String studentId;
    private String username;
    private Integer batch;
    private String phone;
    private String role;
    private String appointedBy;
    private String appointmentReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminInfo fromEntity(Admin admin, boolean includePrivateInfo) {
        return AdminInfo.builder()
                .studentId(includePrivateInfo ? admin.getUser().getStudentId() : null)
                .username(admin.getUser().getUsername())
                .batch(admin.getUser().getBatch())
                .phone(admin.getUser().getPhone())
                .role(admin.getRole())
                .appointedBy(admin.getAppointedBy())
                .appointmentReason(admin.getAppointmentReason())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }
}
