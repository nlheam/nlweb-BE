package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnsembleDTO {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private String artist;
    private String title;
    private String notes;
    private Boolean isActive;
    private Integer memberCount;
    private List<String> vacantSessions;
    private Map<String, MemberInfo> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private String username;
        private Integer batch;
        private String session;
    }
}
