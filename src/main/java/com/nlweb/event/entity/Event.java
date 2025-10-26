package com.nlweb.event.entity;

import com.nlweb.common.enums.EventType;
import com.nlweb.admin.entity.Admin;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.List;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"parentEvent", "childEvents", "createdBy", "rootEvent"})
@EntityListeners(AuditingEntityListener.class)
public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

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

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Setter
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants", nullable = false)
    private Integer currentParticipants;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    @Builder
    public Event(Admin createdBy, Event parentEvent, String title, String description, EventType eventType, Integer depth,
                 LocalDateTime startDateTime, LocalDateTime endDateTime, Integer maxParticipants,
                 Event rootEvent) {
        this.title = title;
        this.description = description;
        this.eventType = eventType;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.parentEvent = parentEvent;
        this.rootEvent = rootEvent;
        this.depth = depth;
        this.createdBy = createdBy;
        this.isActive = true;
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
