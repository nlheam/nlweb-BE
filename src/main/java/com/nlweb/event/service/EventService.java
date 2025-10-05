package com.nlweb.event.service;

import com.nlweb.common.enums.*;
import com.nlweb.common.exception.admin.AdminNotFoundException;
import com.nlweb.common.exception.event.EventNotFoundException;
import com.nlweb.event.dto.*;
import com.nlweb.event.entity.*;
import com.nlweb.event.repository.*;
import com.nlweb.admin.entity.Admin;
import com.nlweb.admin.service.AdminCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventCacheService eventCacheService;
    private final EventParticipantCacheService eventParticipantCacheService;
    private final AdminCacheService adminCacheService;

    @Transactional
    public CreateEventResponse createEvent(CreateEventRequest request, String createdBy) {

        Admin admin = adminCacheService.getAdmin(createdBy)
                .orElseThrow(() -> new AdminNotFoundException("관리자를 찾을 수 없습니다: " + createdBy));

        Event parentEvent = null;
        if (request.getParentEventId() != null) {
            parentEvent = eventCacheService.getEventById(request.getParentEventId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 이벤트를 찾을 수 없습니다. ID: " + request.getParentEventId()));
        }

        Event rootEvent = (parentEvent != null) ? parentEvent.getRootEvent() : null;

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventType(EventType.fromString(request.getEventType()))
                .startDateTime(request.getStartDatetime())
                .endDateTime(request.getEndDatetime())
                .parentEvent(parentEvent)
                .rootEvent(rootEvent)
                .depth((parentEvent != null) ? parentEvent.getDepth() + 1 : 0)
                .createdBy(admin)
                .maxParticipants(request.getMaxParticipants())
                .build();

        Event savedEvent = eventRepository.save(event);

        log.info("이벤트 생성: ID={}, 제목='{}', 생성자='{}'", savedEvent.getId(), savedEvent.getTitle(), createdBy);

        return CreateEventResponse.fromEntity(savedEvent);
    }

    // ================ Create ================

    /** 모든 활성 이벤트 조회 */
    @Transactional
    public List<EventInfo> getActiveEvents() {
        List<Event> events = eventCacheService.getAllActiveEvents();
        return events.stream()
                .map(EventInfo::fromEntity)
                .collect(Collectors.toList());
    }

    // ================= Read =================

    /** 이벤트 ID로 이벤트 조회 */
    @Transactional
    public EventInfo getEventById(Long eventId) {
        Event event = eventCacheService.getEventById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트를 찾을 수 없습니다. ID: " + eventId));
        return EventInfo.fromEntity(event);
    }

    /** 모든 다가오는 이벤트 조회 */
    @Transactional
    public List<EventInfo> getUpcomingEvents() {
        return eventCacheService.getAllUpcomingEvents(LocalDateTime.now())
                .stream()
                .map(EventInfo::fromEntity)
                .collect(Collectors.toList());
    }

    /** 모든 진행 중인 이벤트 조회 */
    @Transactional
    public List<EventInfo> getOngoingEvents() {
        return eventCacheService.getAllOngoingEvents(LocalDateTime.now())
                .stream()
                .map(EventInfo::fromEntity)
                .collect(Collectors.toList());
    }

    /** 모든 기간이 끝난 이벤트 조회 */
    @Transactional
    public List<EventInfo> getAllPastEvents() {
        return eventCacheService.getAllPastEvents(LocalDateTime.now())
                .stream()
                .map(EventInfo::fromEntity)
                .collect(Collectors.toList());
    }

    /** 이벤트 타입별 모든 이벤트 조회 */
    @Transactional
    public List<EventInfo> getAllEventsByType(EventType eventType) {
        return eventCacheService.getAllEventsByEventType(eventType)
                .stream()
                .map(EventInfo::fromEntity)
                .collect(Collectors.toList());
    }

    // ================ Update ================

    /** 이벤트 수정 */
    @Transactional
    public UpdateEventResponse updateEvent(Long eventId, UpdateEventRequest request, String updatedBy) {
        Event event = eventCacheService.getEventById(eventId)
            .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventType() != null) {
            event.setEventType(EventType.fromString(request.getEventType()));
        }
        if (request.getStartDatetime() != null) {
            event.setStartDateTime(request.getStartDatetime());
        }
        if (request.getEndDatetime() != null) {
            event.setEndDateTime(request.getEndDatetime());
        }
        if (request.getMaxParticipants() != null) {
            event.setMaxParticipants(request.getMaxParticipants());
        }

        Event updatedEvent = eventRepository.save(event);

        eventCacheService.evictAllEventCaches();

        log.info("이벤트 수정: ID={}, 제목='{}', 수정자='{}'", updatedEvent.getId(), updatedEvent.getTitle(), updatedBy);

        return UpdateEventResponse.fromEntity(updatedEvent);
    }

    /** 이벤트 활성화 */
    @Transactional
    public void activateEvent(Long eventId, String activatedBy) {
        Event event = eventCacheService.getEventById(eventId)
            .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        if (!adminCacheService.isAdmin(activatedBy)) {
            throw new IllegalStateException("이벤트 활성화 권한이 없습니다.");
        }

        event.activate();
        eventRepository.save(event);

        log.info("이벤트 활성화: ID={}, 제목='{}', 활성화자='{}'", event.getId(), event.getTitle(), activatedBy);
    }

    /** 이벤트 비활성화 */
    @Transactional
    public void deactivateEvent(Long eventId, String deactivatedBy) {
        Event event = eventCacheService.getEventById(eventId)
            .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        if (!adminCacheService.isAdmin(deactivatedBy)) {
            throw new IllegalStateException("이벤트 비활성화 권한이 없습니다.");
        }

        event.deactivate();
        eventRepository.save(event);

        log.info("이벤트 비활성화: ID={}, 제목='{}', 비활성화자='{}'", event.getId(), event.getTitle(), deactivatedBy);
    }

    // ================ Delete ================



}
