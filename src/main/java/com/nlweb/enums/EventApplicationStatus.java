package com.nlweb.enums;

import lombok.Getter;


@Getter
public enum EventApplicationStatus {

    PENDING("대기중"),
    APPROVED("승인됨"),
    REJECTED("거절됨"),
    CANCELLED("취소됨"),
    WITHDRAWN("철회됨");

    private final String description;

    EventApplicationStatus(String description) {
        this.description = description;
    }
}
