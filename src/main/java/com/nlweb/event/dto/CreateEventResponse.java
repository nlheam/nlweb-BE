package com.nlweb.event.dto;

import com.nlweb.event.entity.Event;
import lombok.*;

@Builder
public class CreateEventResponse {

    private EventInfo eventInfo;

    public static CreateEventResponse fromEntity(Event event) {
        return CreateEventResponse.builder()
                .eventInfo(EventInfo.fromEntity(event))
                .build();
    }
}
