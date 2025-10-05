package com.nlweb.ensemble.entity;

import com.nlweb.event.entity.Event;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "ensembles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Ensemble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "artist", nullable = false, length = 100)
    private String artist;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Setter
    @OneToMany(mappedBy = "ensemble", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnsembleMember> members;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

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
    public Ensemble(Event event, String artist, String title, String notes,
                    List<EnsembleMember> members, Boolean isActive) {
        this.event = event;
        this.artist = artist;
        this.title = title;
        this.notes = notes;
        this.members = members;
        this.isActive = isActive;
    }

}
