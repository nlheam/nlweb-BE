package com.nlweb.entity;

import com.nlweb.enums.DayOfWeek;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timeslots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"applications"})
public class Timeslot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ensemble_id", nullable = false)
    private Ensemble ensemble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "excluded_dates", columnDefinition = "jsonb")
    private List<LocalDateTime> excludedDates;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "timeslot", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications;

    @Builder
    public Timeslot(Ensemble ensemble, Event event, DayOfWeek dayOfWeek,
                    LocalTime startTime, LocalTime endTime, List<LocalDateTime> excludedDates) {
        this.ensemble = ensemble;
        this.event = event;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.excludedDates = excludedDates != null ? new ArrayList<>(excludedDates) : new ArrayList<>();
        this.isActive = true;
        this.applications = new ArrayList<>();
    }

    public void addExcludedDate(LocalDateTime date) {
        if (this.excludedDates == null) {
            this.excludedDates = new ArrayList<>();
        }
        this.excludedDates.add(date);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
