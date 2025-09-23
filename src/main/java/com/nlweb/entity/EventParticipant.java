package com.nlweb.entity;

import com.nlweb.enums.EventApplicationStatus;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"event", "user"})
public class EventParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 10)
    private EventApplicationStatus applicationStatus;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Admin approvedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Builder
    public EventParticipant(Event event, User user, EventApplicationStatus applicationStatus,
                            LocalDateTime appliedAt, LocalDateTime approvedAt,
                            Admin approvedBy, String rejectionReason) {
        this.event = event;
        this.user = user;
        this.applicationStatus = applicationStatus;
        this.appliedAt = appliedAt;
        this.approvedAt = approvedAt;
        this.approvedBy = approvedBy;
        this.rejectionReason = rejectionReason;
    }
}
