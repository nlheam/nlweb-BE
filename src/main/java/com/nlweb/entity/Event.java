package com.nlweb.entity;

import com.nlweb.enums.EventType;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"parentEvent", "childEvents", "createdBy", "rootEvent"})
public class Event extends BaseEntity {

    @Setter
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Setter
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private EventType eventType;

    @Setter
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDateTime;

    @Setter
    @Column(name = "end_datetime", nullable = false)
    private LocalDateTime endDateTime;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_event")
    private Event parentEvent;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_event")
    private Event rootEvent;

    @Setter
    @OneToMany(mappedBy = "parentEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> childEvents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Admin createdBy;

    @Column(name = "created_by_name", length = 30, nullable = false)
    private String createdByName;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants;

    @Builder
    public Event(Admin createdBy, Event parentEvent, String title, String description, EventType eventType, Integer depth,
                 LocalDateTime startDateTime, LocalDateTime endDateTime, int maxParticipants, Event rootEvent) {
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.parentEvent = parentEvent;
        this.depth = depth;
        this.rootEvent = rootEvent;
        this.createdBy = createdBy;
        this.createdByName = createdBy.getUser().getUsername();
        this.isActive = false;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void incrementParticipants() {
        this.currentParticipants++;
    }

    public void decrementParticipants() {
        if (this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    public boolean isActive() {
        return this.isActive;
    }
}
