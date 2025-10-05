package com.nlweb.common.enums;

import lombok.Getter;

@Getter
public enum EnsembleSessionType {
    VOCAL("보컬"),
    LEAD_GUITAR("리드기타"),
    RHYTHM_GUITAR("리듬기타"),
    BASS("베이스"),
    DRUM("드럼"),
    KEYBOARD("키보드"),
    SYNTH("신디사이저"),
    NONE("없음");

    private final String description;

    EnsembleSessionType(String description) {
        this.description = description;
    }

    public static EnsembleSessionType fromString(String sessionStr) {
        if (sessionStr == null) {
            return NONE;
        }

        for (EnsembleSessionType session : EnsembleSessionType.values()) {
            if (session.getDescription().equals(sessionStr) ||
                    session.name().equalsIgnoreCase(sessionStr)) {
                return session;
            }
        }

        return NONE;
    }
}
