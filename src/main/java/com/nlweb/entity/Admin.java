package com.nlweb.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor( access = AccessLevel.PROTECTED )
public class Admin extends BaseEntity {


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointed_by")
    private Admin appointedBy;

    @Column(name = "appointed_by_name", length = 30)
    private String appointedByName;

    @Column(name = "appointment_reason", columnDefinition = "TEXT")
    private String appointmentReason;

    @Builder
    public Admin(String role, String appointmentReason, User user, Admin appointedBy) {
        this.user = user;
        this.role = role;
        this.appointedBy = appointedBy;
        this.appointedByName = appointedBy != null ? appointedBy.getUser().getUsername() : "SYSTEM";
        this.appointmentReason = appointmentReason;
    }

    public void updateRole(String newRole) {
        this.role = newRole;
    }

    public String getStudentId(){
        return this.user.getStudentId();
    }
}
