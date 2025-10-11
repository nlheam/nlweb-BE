package com.nlweb.event.dto;

import com.nlweb.event.entity.EventParticipant;
import lombok.*;

@Data
@Builder
public class CreateEventParticipantResponse {

    EventParticipantInfo participantInfo;

    public static CreateEventParticipantResponse fromEntity(EventParticipant participant, boolean includePrivateInfo) {
        return CreateEventParticipantResponse.builder()
                .participantInfo(EventParticipantInfo.fromEntity(participant, includePrivateInfo))
                .build();
    }

}
