package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String studentId;
    private String username;
    private Integer batch;
    private String session;
    private String phone;
    private String email;
    private String status;
    private Boolean isAdmin;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    // 관리자 정보 (관리자인 경우에만)
    private AdminInfoDTO adminInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfoDTO {
        private String role;
        private LocalDateTime appointedDate;
        private String appointedBy;
    }
}
