package com.nlweb.service;

import com.nlweb.dto.request.event.CreateEventRequest;
import com.nlweb.dto.EventDTO;
import com.nlweb.entity.Admin;
import com.nlweb.entity.Event;
import com.nlweb.enums.EventType;
import com.nlweb.exception.event.EventNotFoundException;
import com.nlweb.exception.admin.AdminNotFoundException;
import com.nlweb.repository.AdminRepository;
import com.nlweb.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 이벤트 관리 서비스
 * 합주, 세션, 공연 등 모든 이벤트 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final AdminRepository adminRepository;
//    private final NotificationService notificationService;

    // ========== 이벤트 조회 ==========

    /**
     * 모든 활성 이벤트 조회
     */
    @Cacheable(value = "events:active")
    public List<EventDTO> getActiveEvents() {
        return eventRepository.findByIsActiveTrueOrderByStartDateTimeAsc()
                .stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * 이벤트 상세 조회
     */
    @Cacheable(value = "event", key = "#eventId")
    public EventDTO getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다: " + eventId));

        return convertToEventDTO(event);
    }

    /**
     * 예정된 이벤트 조회
     */
    public List<EventDTO> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingEvents(now)
                .stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * 진행 중인 이벤트 조회
     */
    public List<EventDTO> getOngoingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findOngoingEvents(now)
                .stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * 완료된 이벤트 조회
     */
    public List<EventDTO> getCompletedEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findPastEvents(now)
                .stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * 이벤트 타입별 조회
     */
    public List<EventDTO> getEventsByType(EventType eventType) {
        return eventRepository.findByEventTypeOrderByStartDateTimeAsc(eventType)
                .stream()
                .map(this::convertToEventDTO)
                .collect(Collectors.toList());
    }

    // ========== 이벤트 관리 ==========

    /**
     * 이벤트 생성
     */
    @Transactional
    @CacheEvict(value = {"events:active", "events:upcoming"}, allEntries = true)
    public EventDTO createEvent(CreateEventRequest request, String createdBy) {
        // 관리자 확인
        Admin admin = adminRepository.findByStudentId(createdBy)
                .orElseThrow(() -> new AdminNotFoundException("관리자를 찾을 수 없습니다: " + createdBy));

        // 상위 이벤트 확인 (하위 이벤트인 경우)
        Event parentEvent = null;
        if (request.getParentEventId() != null) {
            parentEvent = eventRepository.findById(request.getParentEventId())
                    .orElseThrow(() -> new EventNotFoundException("상위 이벤트를 찾을 수 없습니다: " + request.getParentEventId()));
        }

        Event rootEvent = (parentEvent != null) ? parentEvent.getRootEvent() : null;

        // 이벤트 생성
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventType(EventType.valueOf(request.getEventType()))
                .startDateTime(request.getStartDatetime())
                .endDateTime(request.getEndDatetime())
                .parentEvent(parentEvent)
                .depth(parentEvent != null ? parentEvent.getDepth() + 1 : 0)
                .rootEvent(rootEvent)
                .createdBy(admin)
                .maxParticipants(request.getMaxParticipants() != null ? request.getMaxParticipants() : 200)
                .build();

        Event savedEvent = eventRepository.save(event);

        // 이벤트 생성 알림 발송
        sendEventCreationNotification(savedEvent, admin);

        log.info("이벤트 생성: {} by {}", savedEvent.getTitle(), createdBy);

        return convertToEventDTO(savedEvent);
    }

    /**
     * 이벤트 수정
     */
    @Transactional
    @CacheEvict(value = {"event", "events:active", "events:upcoming"}, allEntries = true)
    public EventDTO updateEvent(Long eventId, CreateEventRequest request, String updatedBy) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다: " + eventId));

        // 권한 확인 (이벤트 생성자 또는 상위 관리자만)
        if (!adminRepository.existsByStudentId(updatedBy)) {
            throw new IllegalStateException("이벤트 수정 권한이 없습니다");
        }

        // 이벤트 정보 업데이트
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventType(EventType.valueOf(request.getEventType()));
        event.setStartDateTime(request.getStartDatetime());
        event.setEndDateTime(request.getEndDatetime());

        Event updatedEvent = eventRepository.save(event);

        // 이벤트 수정 알림 발송
        sendEventUpdateNotification(updatedEvent, updatedBy);

        log.info("이벤트 수정: {} by {}", updatedEvent.getTitle(), updatedBy);

        return convertToEventDTO(updatedEvent);
    }

    /**
     * 이벤트 비활성화
     */
    @Transactional
    @CacheEvict(value = {"event", "events:active", "events:upcoming"}, allEntries = true)
    public void deleteEvent(Long eventId, String deletedBy) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다: " + eventId));

        // 권한 확인
        if (!adminRepository.existsByStudentId(deletedBy)) {
            throw new IllegalStateException("이벤트 삭제 권한이 없습니다");
        }

        // 진행 중인 이벤트는 삭제 불가
        if (event.isActive()) {
            throw new IllegalStateException("진행 중인 이벤트는 삭제할 수 없습니다");
        }

        event.deactivate();
        eventRepository.save(event);

        // 이벤트 삭제 알림 발송
        sendEventDeletionNotification(event, deletedBy);

        log.info("이벤트 삭제: {} by {}", event.getTitle(), deletedBy);
    }

    // ========== 참가자 관리 ==========

    /**
     * 참가자 추가
     */
    @Transactional
    @CacheEvict(value = "event", key = "#eventId")
    public void addParticipant(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다: " + eventId));

        event.incrementParticipants();
        eventRepository.save(event);

        log.debug("이벤트 참가자 추가: {} (현재 {}명)",
                event.getTitle(), event.getCurrentParticipants());
    }

    /**
     * 참가자 제거
     */
    @Transactional
    @CacheEvict(value = "event", key = "#eventId")
    public void removeParticipant(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다: " + eventId));

        event.decrementParticipants();
        eventRepository.save(event);

        log.debug("이벤트 참가자 제거: {} (현재 {}명)",
                event.getTitle(), event.getCurrentParticipants());
    }

    // ========== Private 메서드들 ==========

    private boolean canModifyEvent(Event event, String userStudentId) {
        // 상위 관리자인지 확인 (시스템 관리자 등)
        Admin admin = adminRepository.findByStudentId(userStudentId).orElse(null);
        return admin != null;
    }

    private void sendEventCreationNotification(Event event, Admin creator) {
        // 모든 활성 사용자에게 새 이벤트 알림 발송 (비동기)
        // 구현 생략 (대량 발송)
    }

    private void sendEventUpdateNotification(Event event, String updatedBy) {
        // 이벤트 참가자들에게 수정 알림 발송
        // 구현 생략
    }

    private void sendEventDeletionNotification(Event event, String deletedBy) {
        // 이벤트 참가자들에게 취소 알림 발송
        // 구현 생략
    }

    private EventDTO convertToEventDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .parentEventId(event.getParentEvent() != null ? event.getParentEvent().getId() : null)
                .title(event.getTitle())
                .description(event.getDescription())
                .eventType(event.getEventType().name())
                .startDatetime(event.getStartDateTime())
                .endDatetime(event.getEndDateTime())
                .isActive(event.getIsActive())
                .currentParticipants(event.getCurrentParticipants())
                .status(getEventStatus(event))
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .createdBy(convertToAdminInfoDTO(event.getCreatedBy()))
                .build();
    }


    private String getEventStatus(Event event) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = event.getStartDateTime();
        LocalDateTime endTime = event.getEndDateTime();

        if (now.isAfter(endTime)) {
            return "FINISHED";
        }

        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return "ONGOING";
        }

        return "UPCOMING";
    }

    private EventDTO.AdminInfoDTO convertToAdminInfoDTO(Admin admin) {
        return EventDTO.AdminInfoDTO.builder()
                .studentId(admin.getStudentId())
                .username(admin.getUser().getUsername())
                .role(admin.getRole())
                .build();
    }

    // 통계 DTO
    @lombok.Data
    @lombok.Builder
    public static class EventStatistics {
        private Long totalEvents;
        private List<Object[]> typeStatistics;
        private List<Object[]> monthlyStatistics;
        private List<Object[]> adminStatistics;
    }
}
