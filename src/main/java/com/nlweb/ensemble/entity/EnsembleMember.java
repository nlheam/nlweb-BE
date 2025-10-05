package com.nlweb.ensemble.entity;

import com.nlweb.common.enums.EnsembleSessionType;
import com.nlweb.user.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ensemble_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class EnsembleMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ensemble_id", nullable = false)
    private Ensemble ensemble;

    @Column(name = "session", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EnsembleSessionType session;

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
    public EnsembleMember(User user, Ensemble ensemble, EnsembleSessionType session) {
        this.user = user;
        this.ensemble = ensemble;
        this.session = session;
    }

}
