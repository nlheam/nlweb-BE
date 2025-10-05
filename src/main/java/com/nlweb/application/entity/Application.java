package com.nlweb.application.entity;

import com.nlweb.common.enums.ApplicationStatus;
import com.nlweb.common.enums.ApplicationType;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_type", nullable = false, length = 20)
    private ApplicationType applicationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false, length = 20)
    private ApplicationStatus applicationStatus;

    @Column(name = "applied_to")
    private Long appliedTo;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", columnDefinition = "jsonb")
    private String details;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;


}
