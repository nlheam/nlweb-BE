package com.nlweb.event.dto;

import lombok.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Data
public class UpdateEventRequest {

    private String title;

    private String description;

    private String eventType; // ENSEMBLE_STUDY, SESSION_STUDY, etc.

    private LocalDateTime startDatetime;

    private LocalDateTime endDatetime;

    private Integer maxParticipants;

}
