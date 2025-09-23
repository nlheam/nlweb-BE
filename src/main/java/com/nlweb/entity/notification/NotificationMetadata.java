package com.nlweb.entity.notification;//package com.nlweb.common.entity.notification.;
//
//import lombok.*;
//import jakarta.persistence.*;
//
//
//@Entity
//@Table(name = "notification_metadata")
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@EqualsAndHashCode(of = {"notification", "key"})
//public class NotificationMetadata {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "notification_id", nullable = false)
//    private NotificationEntity notification;
//
//    @Column(name = "meta_key", nullable = false)
//    private String key;
//
//    @Column(name = "meta_value")
//    private String value;
//
//    @Builder
//    public NotificationMetadata(NotificationEntity notification, String key, String value) {
//        this.notification = notification;
//        this.key = key;
//        this.value = value;
//    }
//}