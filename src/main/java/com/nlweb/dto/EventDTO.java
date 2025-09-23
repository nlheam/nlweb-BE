package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    private Long id;
    private String title;
    private String description;
    private String eventType;

    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    private Long parentEventId;
    private Integer depth;
    private Long rootEventId;

    private Boolean isActive;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status; // UPCOMING, ONGOING, FINISHED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 관련 데이터
    private AdminInfoDTO createdBy;
    private List<EnsembleDTO> ensembles;
    private List<EventDTO> childEvents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminInfoDTO {
        private String studentId;
        private String username;
        private String role;
    }
}
