package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeslotDTO {

    private Long id;
    private String artist;
    private String title;
    private String dayOfWeek; // e.g., MONDAY, TUESDAY
    private String startTime; // e.g., "14:00"
    private String endTime;   // e.g., "15:00"
    private List<String> excludedDates; // e.g., ["2023-12-25", "2024-01-01"]
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 관련 데이터
    private EnsembleDTO ensemble;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnsembleDTO {
        private Long id;
        private Long eventId;
        private String eventTitle;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipationSummary {
        private Long totalVotes;
        private Long attendingCount;
        private Long notAttendingCount;
    }
}
