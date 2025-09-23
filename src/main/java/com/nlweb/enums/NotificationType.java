package com.nlweb.enums;

import lombok.Getter;

/**
 * 알림 타입 열거형
 */
@Getter
public enum NotificationType {
    APPLICATION_RESULT("신청 결과"),
    EVENT_REMINDER("이벤트 알림"),
    SCHEDULE_CHANGE("일정 변경"),
    SYSTEM_NOTICE("시스템 공지"),
    ADMIN_MESSAGE("관리자 메시지");

    // ENSEMBLE_INVITE("합주 초대") 합주 플러팅 기능은 아직

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }
}
