package com.nlweb.common.enums;

import lombok.Getter;

@Getter
public enum ApplicationType {
    EVENT("이벤트 참가 신청"),
    ENSEMBLE("합주 신청"),
    SESSION("세션 신청"),
    TIMESLOT("시간표 신청");

    private final String description;

    ApplicationType(String description) {
        this.description = description;
    }
}
