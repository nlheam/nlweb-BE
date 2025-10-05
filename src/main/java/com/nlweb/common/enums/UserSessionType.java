package com.nlweb.common.enums;

import lombok.Getter;

@Getter
public enum UserSessionType {
    VOCAL("보컬"),
    GUITAR("기타"),
    BASS("베이스"),
    DRUM("드럼"),
    KEYBOARD("키보드"),
    NONE("없음"); // super 관리자용

    private final String description;

    UserSessionType(String description) {
        this.description = description;
    }


    public static UserSessionType fromString(String sessionStr) {
        if (sessionStr == null) {
            return NONE;
        }

        for (UserSessionType session : UserSessionType.values()) {
            if (session.getDescription().equals(sessionStr) ||
                    session.name().equalsIgnoreCase(sessionStr)) {
                return session;
            }
        }

        return NONE;
    }
}
