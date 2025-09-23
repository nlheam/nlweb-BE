package com.nlweb.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING("대기중"),        // 가입 신청 후 승인 대기
    ACTIVE("활동중"),         // 활동 중
    INACTIVE("비활동중"),      // 졸업, 휴학, 군대 등으로 비활동
    REJECTED("거절됨"),       // 가입 신청이 거절됨
    SUSPENDED("정지됨"),      // 집부 권한으로 정지 (특별한 사유)
    DELETED("탈퇴됨");        // 회원 탈퇴 (데이터는 남아있음)

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public static UserStatus fromString(String statusStr) {
        if (statusStr == null) {
            return PENDING;
        }

        for (UserStatus status : UserStatus.values()) {
            if (status.getDescription().equals(statusStr) ||
                    status.name().equalsIgnoreCase(statusStr)) {
                return status;
            }
        }

        return PENDING;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isInActive() {
        return this == INACTIVE;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
