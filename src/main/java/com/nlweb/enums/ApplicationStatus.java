package com.nlweb.enums;

import lombok.Getter;


@Getter
public enum ApplicationStatus {
    SUCCESS("성공"),
    FAILURE("실패"),
    CONFLICT("충돌"),
    CANCELLED("취소됨"),
    TIMEOUT("시간초과"),
    VALIDATION_ERROR("유효성 검사 오류");

    private final String description;

    ApplicationStatus(String description) {
        this.description = description;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public boolean isFailed() {
        return this == FAILURE || this == CONFLICT;
    }
}
