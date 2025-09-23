package com.nlweb.entity;

import com.nlweb.enums.ApplicationStatus;
import com.nlweb.enums.ApplicationType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Table(name = "applications")
@Getter
@NoArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_type", nullable = false, length = 20)
    private ApplicationType applicationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 20)
    private ApplicationStatus applicationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ensemble_id")
    private Ensemble ensemble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id")
    private Timeslot timeslot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    public Application(User user, ApplicationType applicationType, ApplicationStatus applicationStatus,
                       Event event, Ensemble ensemble, Timeslot timeslot,
                       Map<String, Object> details, String errorCode, String errorMessage) {
        this.user = user;
        this.applicationType = applicationType;
        this.applicationStatus = applicationStatus;
        this.event = event;
        this.ensemble = null;
        this.timeslot = null;
        this.details = details;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public boolean isEventApplication() {
        return this.applicationType == ApplicationType.EVENT;
    }

    public boolean isEnsembleApplication() {
        return this.applicationType == ApplicationType.ENSEMBLE;
    }

    public boolean isSessionApplication() {
        return this.applicationType == ApplicationType.SESSION;
    }

    public boolean isTimeslotApplication() {
        return this.applicationType == ApplicationType.TIMESLOT;
    }

    public boolean isSuccessful() {
        return this.applicationStatus.isSuccessful();
    }
}
