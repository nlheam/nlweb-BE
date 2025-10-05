package com.nlweb.event.dto;

import com.nlweb.event.entity.Event;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Data
@Builder
public class EventInfo {

    private String title;
    private String description;
    private String eventType;
    private Boolean isActive;
    private Boolean isVotable;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long parentEventId;
    private Long rootEventId;
    private Integer depth;
    private List<Long> childEventIds;
    private String createdByStudentId;

    public static EventInfo fromEntity(Event event) {
        return EventInfo.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .eventType(event.getEventType().toString())
                .isActive(event.getIsActive())
                .isVotable(event.getIsVotable())
                .maxParticipants(event.getMaxParticipants())
                .currentParticipants(event.getCurrentParticipants())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .parentEventId(event.getParentEvent() != null ? event.getParentEvent().getId() : null)
                .rootEventId(event.getRootEvent() != null ? event.getRootEvent().getId() : null)
                .depth(event.getDepth())
                .childEventIds(event.getChildEvents() != null ?
                        event.getChildEvents().stream().map(Event::getId).toList() : null)
                .createdByStudentId(event.getCreatedBy() != null ? event.getCreatedBy().getStudentId() : null)
                .build();
    }

}
