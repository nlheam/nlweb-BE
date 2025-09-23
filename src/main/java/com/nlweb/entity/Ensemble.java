package com.nlweb.entity;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "ensembles")
@Getter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Ensemble extends BaseEntity {

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
    private List<EnsembleParticipant> participants;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Builder
    public Ensemble(Event event, String artist, List<EnsembleParticipant> participants, String title, String notes) {
        this.event = event;
        this.artist = artist;
        this.title = title;
        this.participants = participants;
        this.notes = notes;
        this.isActive = true;
    }
}
