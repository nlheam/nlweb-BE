package com.nlweb.entity.notification;//package com.nlweb.common.entity.notification;
//
//import com.nlweb.common.entity.BaseEntity;
//import com.nlweb.common.entity.User;
//import com.nlweb.common.enums.NotificationPriority;
//import com.nlweb.common.enums.NotificationType;
//import lombok.*;
//import jakarta.persistence.*;
//import java.util.*;
//
//@Entity
//@Table(name = "notifications")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class NotificationEntity extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_id", nullable = false)
//    private User user;
//
//    @Column(name = "title", nullable = false, length = 200)
//    private String title;
//
//    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
//    private String message;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "notification_type", nullable = false, length = 30)
//    private NotificationType notificationType;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "priority", nullable = false, length = 10)
//    private NotificationPriority notificationPriority;
//
//    @Column(name = "action_url", length = 500)
//    private String actionUrl;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sender_id")
//    private User sender;
//
//    @Setter
//    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<NotificationMetadata> metadata = new HashSet<>();
//
//    @Setter
//    @OneToOne(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
//    private NotificationStatus status;
//}
