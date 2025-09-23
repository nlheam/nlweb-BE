package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 알림 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime expiresAt;
    private String actionUrl;
    private String senderName;
    private Boolean isExpired;
    private Boolean isNew; // 30분 이내 생성
    private Map<String, String> metaData;

    /**
     * 읽음 상태에 따른 CSS 클래스
     */
    public String getCssClass() {
        if (isExpired != null && isExpired) return "notification-expired";
        if (isRead != null && !isRead) return "notification-unread";
        return "notification-read";
    }

    /**
     * 우선순위 아이콘
     */
    public String getPriorityIcon() {
        if (priority == null) return "info";
        return switch (priority.toUpperCase()) {
            case "URGENT" -> "warning";
            case "HIGH" -> "exclamation";
            case "NORMAL" -> "info";
            case "LOW" -> "check";
            default -> "info";
        };
    }
}
