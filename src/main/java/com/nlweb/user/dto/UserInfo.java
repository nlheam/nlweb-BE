package com.nlweb.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nlweb.user.entity.User;
import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo {

    private String studentId;
    private String username;
    private Integer batch;
    private String session;
    private String phone;
    private String email;
    private String status;
    private Boolean isVocalable;
    private Boolean isAdmin;
    private AdminInfo adminInfo;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdminInfo {

        private String role;
        private String appointedBy;
        private String appointmentReason;
        private String createdAt;
        private String updatedAt;

        public static AdminInfo fromEntity(User user) {
            if (user.getAdmin() == null) {
                return null;
            }
            return AdminInfo.builder()
                    .role(user.getAdmin().getRole())
                    .appointedBy(user.getAdmin().getAppointedBy())
                    .appointmentReason(user.getAdmin().getAppointmentReason())
                    .createdAt(user.getAdmin().getCreatedAt().toString())
                    .updatedAt(user.getAdmin().getUpdatedAt().toString())
                    .build();
        }

    }

    public static UserInfo fromEntity(User user, boolean includePrivateInfo) {
        return UserInfo.builder()
                .studentId(includePrivateInfo ? user.getStudentId() : null)
                .username(user.getUsername())
                .batch(user.getBatch())
                .session(user.getSession().toString())
                .phone(includePrivateInfo ? user.getPhone(): null)
                .email(user.getEmail())
                .status(user.getStatus().toString())
                .isVocalable(user.getIsVocalable())
                .isAdmin(user.getIsAdmin())
                .adminInfo(user.isAdmin() ? AdminInfo.fromEntity(user) : null)
                .lastLogin(includePrivateInfo ? user.getLastLogin() : null)
                .createdAt(includePrivateInfo ? user.getCreatedAt() : null)
                .build();
    }

}
