package com.nlweb.common.enums;

import lombok.Getter;

@Getter
public enum EventType {

    ENSEMBLE_STUDY("합주스터디"),
    SESSION_STUDY("세션스터디"),
    EVENT_APPLICATION("이벤트 참여 신청"),
    ENSEMBLE_APPLICATION("합주 신청"),
    SESSION_APPLICATION("세션 신청"),
    TIMESLOT_APPLICATION("시간표 신청"),
    REGULAR_CONCERT("정기 공연"),
    EXTRA_EVENT("기타 이벤트"); // 축제, 버스킹 등..

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public boolean isApplicationType() {
        return name().contains("APPLICATION");
    }

    public boolean isStudyType() {
        return name().contains("STUDY");
    }

    public boolean isConcertType() {
        return this == REGULAR_CONCERT;
    }

    static public EventType fromString(String value) {
        for (EventType type : EventType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.getDescription().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

}
