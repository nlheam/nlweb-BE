package com.nlweb.entity;

import com.nlweb.enums.EnsembleSessionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "ensemble_participants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ensemble_id", "session_type"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnsembleParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ensemble_id", nullable = false)
    private Ensemble ensemble;

    @Column(name = "session_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnsembleSessionType ensembleSessionType;

    @Builder
    public EnsembleParticipant(User user, Ensemble ensemble, EnsembleSessionType ensembleSessionType) {
        this.user = user;
        this.ensemble = ensemble;
        this.ensembleSessionType = ensembleSessionType;
    }
}
