package com.nlweb.user.entity;

import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.enums.UserStatus;
import com.nlweb.admin.entity.Admin;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "student_id", length = 8, nullable = false, unique = true)
    private String studentId;

    @Column(name = "username", length = 30, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Setter
    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "batch", nullable = false)
    private Integer batch;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "session", nullable = false, length = 10)
    private UserSessionType session;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Setter
    @Column(name = "is_vocalable", nullable = false)
    @ColumnDefault("false")
    private Boolean isVocalable;

    @Setter
    @Column(name = "is_admin", nullable = false)
    @ColumnDefault("false")
    private Boolean isAdmin = false;

    @JsonIgnore
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version")
    private Long version;

    public boolean isAdmin() {
        return this.isAdmin != null && this.isAdmin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
        this.isAdmin = true;
    }

    public void revokeAdmin() {
        this.admin = null;
        this.isAdmin = false;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public boolean isVocalable() {
        return this.session == UserSessionType.VOCAL || this.isVocalable;
    }

    public String getPasswordHash() {
        return this.password;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public boolean canBeHardDeleted() {
        LocalDateTime now = LocalDateTime.now();
        return this.status.isDeleted() && this.lastLogin.plusMonths(6).isBefore(now);
    }

    @Builder
    public User(String studentId, String username, String password, String email,
                String phone, Integer batch, UserSessionType session, UserStatus status) {

        this.studentId = studentId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.batch = batch;
        this.session = session;
        this.status = status;
        this.isVocalable = session.equals(UserSessionType.VOCAL);
        this.isAdmin = false;
        this.lastLogin = LocalDateTime.now();
    }

}
