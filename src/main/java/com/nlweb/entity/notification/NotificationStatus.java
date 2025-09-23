package com.nlweb.entity.notification;//package com.nlweb.common.entity.notification.;
//
//import lombok.*;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "notifications")
//@EntityListeners(AuditingEntityListener.class)
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Builder
//@AllArgsConstructor
//public class NotificationStatus {
//
//    @Id
//    private Long id;
//
//    @MapsId
//    @Setter
//    @OneToOne
//    @JoinColumn(name = "notification_id")
//    private NotificationEntity notification;
//
//    @Column(name = "is_read", nullable = false)
//    @Builder.Default
//    private Boolean isRead = false;
//
//    @Column(name = "read_at")
//    private LocalDateTime readAt;
//
//    @Column(name = "is_deleted", nullable = false)
//    @Builder.Default
//    private Boolean isDeleted = false;
//
//    @Column(name = "delivered_at")
//    private LocalDateTime deliveredAt;
//
//    @Column(name = "expires_at")
//    private LocalDateTime expiresAt;
//
//    public void markAsRead() {
//        this.isRead = true;
//        this.readAt = LocalDateTime.now();
//    }
//
//    public void markAsDeleted() {
//        this.isDeleted = true;
//    }
//}
