package com.nlweb.common.enums;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String koreanName;

    DayOfWeek(String koreanName) {
        this.koreanName = koreanName;
    }

    public static DayOfWeek fromJavaTime(java.time.DayOfWeek javaDayOfWeek) {
        return valueOf(javaDayOfWeek.name());
    }
}
