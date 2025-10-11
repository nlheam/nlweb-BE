package com.nlweb.event.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {

    @NotBlank(message = "이벤트 제목은 필수입니다")
    @Size(min = 2, max = 200, message = "제목은 2자 이상 200자 이하여야 합니다")
    private String title;

    @Size(max = 2000, message = "설명은 2000자를 초과할 수 없습니다")
    private String description;

    @NotBlank(message = "이벤트 타입은 필수입니다")
    private String eventType; // ENSEMBLE_STUDY, SESSION_STUDY, etc.

    @NotNull(message = "시작 일시는 필수입니다")
    @Future(message = "시작 일시는 현재 시간 이후여야 합니다")
    private LocalDateTime startDatetime;

    @NotNull(message = "종료 일시는 필수입니다")
    @Future(message = "종료 일시는 현재 시간 이후여야 합니다")
    private LocalDateTime endDatetime;

    @NotNull(message = "참여 투표 시작 일시는 필수입니다")
    @Future(message = "참여 투표 시작 일시는 현재 시간 이후여야 합니다")
    private LocalDateTime votingStartDateTime;

    @NotNull(message = "참여 투표 종료 일시는 필수입니다")
    @Future(message = "참여 투표 종료 일시는 현재 시간 이후여야 합니다")
    private LocalDateTime votingEndDateTime;

    private Long parentEventId;

    @Min(value = 1, message = "최대 참가자 수는 1명 이상이어야 합니다")
    private Integer maxParticipants;

    @AssertTrue(message = "종료 시간은 시작 시간보다 늦어야 합니다")
    public boolean isValidDateRange() {
        return endDatetime == null || startDatetime == null || endDatetime.isAfter(startDatetime);
    }

}
