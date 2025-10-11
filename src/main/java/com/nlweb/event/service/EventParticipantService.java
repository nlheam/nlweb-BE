package com.nlweb.event.service;

import com.nlweb.common.exception.event.EventNotFoundException;
import com.nlweb.common.exception.user.UserNotFoundException;
import com.nlweb.event.dto.*;
import com.nlweb.event.entity.*;
import com.nlweb.user.entity.User;
import com.nlweb.user.service.UserCacheService;
import com.nlweb.admin.service.AdminCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantService {

    private final EventCacheService eventCacheService;
    private final EventParticipantCacheService eventParticipantCacheService;
    private final AdminCacheService adminCacheService;
    private final UserCacheService userCacheService;

    // ================ Create ================

    /** 이벤트 참가자 생성 */
    @Transactional
    public CreateEventParticipantResponse createEventParticipant(String createdBy, Long eventId, CreateEventParticipantRequest request, Boolean includePrivateInfo) {

        Event event = eventCacheService.getEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        Event rootEvent = event.getRootEvent() != null ? event.getRootEvent() : event;

        User user = userCacheService.getUserByStudentId(request.getStudentId())
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. 학번: " + createdBy));

        if (!Objects.equals(request.getStudentId(), createdBy)) {
            if (!adminCacheService.isAdmin(createdBy)) {
                log.warn("사용자 {}는 다른 사용자 {}의 이벤트 참가를 시도했습니다. 권한이 없습니다.", createdBy, request.getStudentId());
                throw new IllegalStateException("다른 사용자의 이벤트 참가를 생성할 권한이 없습니다.");
            }
        }

        if (eventParticipantCacheService.existsByEventIdAndUserId(rootEvent.getId(), user.getId())) {
            log.warn("사용자 {}는 이미 이벤트 {}에 참가했습니다.", createdBy, event.getId());
            throw new IllegalStateException("이미 이벤트에 참가한 사용자입니다.");
        }

        if (rootEvent.getMaxParticipants() != null && rootEvent.getCurrentParticipants() >= rootEvent.getMaxParticipants()) {
            log.warn("이벤트 {}의 참가자 수가 최대치를 초과했습니다.", event.getId());
            throw new IllegalStateException("이벤트의 최대 참가자 수를 초과했습니다.");
        }

        rootEvent.incrementParticipants();
        eventCacheService.saveEvent(rootEvent);

        EventParticipant participant = EventParticipant.builder()
                .event(rootEvent)
                .user(user)
                .appliedAt(LocalDateTime.now())
                .build();

        EventParticipant savedParticipant = eventParticipantCacheService.saveEventParticipant(participant);

        return CreateEventParticipantResponse.fromEntity(savedParticipant, includePrivateInfo);
    }

    // ================= Read =================

    /** 이벤트 ID로 참가자 정보 조회 */
    @Transactional
    public List<EventParticipantInfo> getAllParticipantsByEventId(Long eventId, Boolean includePrivateInfo) {
        return eventParticipantCacheService.getParticipantsByEventId(eventId).stream()
                .map(participant -> EventParticipantInfo.fromEntity(participant, includePrivateInfo))
                .collect(Collectors.toList());
    }

    /** 사용자 ID로 참가자 정보 조회 */
    @Transactional
    public List<EventParticipantInfo> getAllParticipantsByUserId(Long userId, Boolean includePrivateInfo) {
        return eventParticipantCacheService.getParticipantsByUserId(userId).stream()
                .map(participant -> EventParticipantInfo.fromEntity(participant, includePrivateInfo))
                .collect(Collectors.toList());
    }

    // ================ Delete ================

    /** 이벤트 참가자 삭제 */
    @Transactional
    public void deleteEventParticipant(Long eventId, String studentId, String deletedBy) {

        Event event = eventCacheService.getEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("이벤트를 찾을 수 없습니다. ID: " + eventId));

        Event rootEvent = event.getRootEvent() != null ? event.getRootEvent() : event;

        User user = userCacheService.getUserByStudentId(studentId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다. 학번: " + studentId));

        if (!Objects.equals(studentId, deletedBy)) {
            if (!adminCacheService.isAdmin(deletedBy)) {
                log.warn("사용자 {}는 다른 사용자 {}의 이벤트 참가자 삭제를 시도했습니다. 권한이 없습니다.", deletedBy, studentId);
                throw new IllegalStateException("다른 사용자의 이벤트 참가자 삭제 권한이 없습니다.");
            }
        }

        if (!eventParticipantCacheService.existsByEventIdAndUserId(rootEvent.getId(), user.getId())) {
            log.warn("사용자 {}는 이벤트 {}에 참가하지 않았습니다.", studentId, event.getId());
            throw new IllegalStateException("이벤트에 참가하지 않은 사용자입니다.");
        }

        EventParticipant participant = eventParticipantCacheService.getParticipantByEventIdAndUserId(event.getId(), user.getId());

        rootEvent.decrementParticipants();
        eventCacheService.saveEvent(rootEvent);

        eventParticipantCacheService.deleteEventParticipant(participant);
        log.info("이벤트 참가자 삭제: 이벤트ID={}, 사용자ID={}, 삭제자ID={}", eventId, studentId, deletedBy);
    }

}
