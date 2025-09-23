package com.nlweb.enums;

import lombok.Getter;


@Getter
public enum NotificationPriority {
    LOW("낮음"),
    NORMAL("보통"),
    HIGH("높음"),
    URGENT("긴급");

    private final String description;

    NotificationPriority(String description) {
        this.description = description;
    }

    public boolean isHighPriority() {
        return this == HIGH || this == URGENT;
    }

    public int getPriorityLevel() {
        return ordinal() + 1;
    }
}
