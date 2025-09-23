package com.nlweb.entity;

import com.nlweb.enums.UserSessionType;
import com.nlweb.enums.UserStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

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

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Setter
    @Column(name = "is_vocalable", nullable = false)
    private boolean isVocalable;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Admin admin;

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
        this.isVocalable = isVocalable();
        this.lastLogin = LocalDateTime.now();
        this.admin = null;
    }

    public boolean isAdmin() {
        return this.admin != null;
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


    public boolean canHardDelete() {
        LocalDateTime now = LocalDateTime.now();
        return this.status.isDeleted() && this.lastLogin.plusMonths(1).isBefore(now);
    }
}
