package com.nlweb.event.dto;

import com.nlweb.event.entity.EventParticipant;
import com.nlweb.user.dto.UserInfo;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
public class EventParticipantInfo {

    private Long EventId;
    private Long userId;
    private UserInfo user;
    private LocalDateTime createdDate;

    public static EventParticipantInfo fromEntity(EventParticipant participant, boolean includePrivateInfo) {
        return EventParticipantInfo.builder()
                .EventId(participant.getEvent().getId())
                .userId(participant.getUser().getId())
                .user(UserInfo.fromEntity(participant.getUser(), includePrivateInfo))
                .build();
    }

}
