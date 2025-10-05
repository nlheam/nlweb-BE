package com.nlweb.event.dto;

import com.nlweb.event.entity.Event;
import lombok.*;

@Data
@Builder
public class UpdateEventResponse {

    private EventInfo eventInfo;

    public static UpdateEventResponse fromEntity(Event event) {
        return UpdateEventResponse.builder()
                .eventInfo(EventInfo.fromEntity(event))
                .build();
    }
}
