package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 관리자 정보 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {

    private String studentId;
    private String username;
    private Integer batch;
    private String role;
    private LocalDateTime appointedDate;
    private String appointedBy;
    private String appointmentReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
