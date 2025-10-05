package com.nlweb.admin.entity;

import com.nlweb.user.entity.User;
import lombok.*;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
@EntityListeners(AuditingEntityListener.class)
public class Admin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter
    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Column(name = "appointed_by", length = 8, nullable = false)
    private String appointedBy;

    @Column(name = "appointment_reason", columnDefinition = "TEXT")
    private String appointmentReason;

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
    public Admin(String role, String appointmentReason, User user, String appointedBy) {
        this.user = user;
        this.role = role;
        this.appointedBy = appointedBy;
        this.appointmentReason = appointmentReason;
    }

    public String getStudentId() {
        return user.getStudentId();
    }
}
