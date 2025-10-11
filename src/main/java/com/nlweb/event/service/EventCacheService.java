package com.nlweb.event.service;

import com.nlweb.common.enums.EventType;
import com.nlweb.event.repository.EventRepository;
import com.nlweb.event.entity.Event;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventCacheService {

    private final EventRepository eventRepository;

    /** 이벤트 ID로 이벤트 조회 */
    @Cacheable(value = "event", key = "#id", unless = "#result == null")
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    /** 모든 이벤트 조회 */
    @Cacheable(value = "events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /** 모든 활성화 이벤트 조회 */
    @Cacheable(value = "events:active")
    public List<Event> getAllActiveEvents() {
        return eventRepository.findByIsActiveTrueOrderByStartDateTimeAsc();
    }

    /** 모든 다가오는 이벤트 조회 */
    @Cacheable(value = "events:upcoming")
    public List<Event> getAllUpcomingEvents(LocalDateTime now) {
        return eventRepository.findUpcomingEvents(now);
    }

    /** 모든 진행 중인 이벤트 조회 */
    @Cacheable(value = "events:ongoing")
    public List<Event> getAllOngoingEvents(LocalDateTime now) {
        return eventRepository.findOngoingEvents(now);
    }

    /** 모든 기간이 끝난 이벤트 조회 */
    @Cacheable(value = "events:past")
    public List<Event> getAllPastEvents(LocalDateTime now) {
        return eventRepository.findPastEvents(now);
    }

    /** 이벤트 타입별 모든 활성화 이벤트 조회 */
    @Cacheable(value = "event", key = "#eventType")
    public List<Event> getAllEventsByEventType(EventType eventType) {
        return eventRepository.findByEventTypeOrderByStartDateTimeAsc(eventType);
    }

    /** 이벤트 ID로 이벤트 존재 여부 확인 */
    @Cacheable(value = "event", key = "#id")
    public boolean isEventExistsById(Long id) {
        return eventRepository.existsById(id);
    }

    /** 모든 이벤트 캐시 삭제 */
    @CacheEvict(value = {
            "event", "events", "events:active", "events:upcoming", "events:votable", "events:ongoing", "events:past"
    }, allEntries = true)
    public void evictAllEventCaches() {
        log.debug("모든 이벤트 캐시 삭제");
    }

    /** 이벤트 저장 및 캐시 무효화 */
    @Transactional
    public Event saveEvent(Event event) {
        Event savedEvent = eventRepository.save(event);
        evictAllEventCaches(); // 캐시 무효화
        return savedEvent;
    }

    /** 이벤트 삭제 및 캐시 무효화 */
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
        evictAllEventCaches(); // 캐시 무효화
    }

}
