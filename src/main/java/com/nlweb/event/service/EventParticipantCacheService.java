package com.nlweb.event.service;

import com.nlweb.event.repository.EventParticipantRepository;
import com.nlweb.event.entity.EventParticipant;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantCacheService {

    private final EventParticipantRepository eventParticipantRepository;

    /** 이벤트 ID로 참가자 목록 조회 */
    @Cacheable(value = "eventParticipantsByEvent", key = "#eventId")
    public List<EventParticipant> getParticipantsByEventId(Long eventId) {
        log.debug("이벤트 ID {}에 대한 참가자 목록을 캐시에서 조회합니다.", eventId);
        return eventParticipantRepository.findByEventId(eventId);
    }

    /** 사용자 ID로 참가자 목록 조회 */
    @Cacheable(value = "eventParticipantsByUser", key = "#userId")
    public List<EventParticipant> getParticipantsByUserId(Long userId) {
        log.debug("사용자 ID {}에 대한 참가자 목록을 캐시에서 조회합니다.", userId);
        return eventParticipantRepository.findByUserId(userId);
    }

    /** 이벤트 ID와 사용자 ID로 참가자 조회 */
    @Cacheable(value = "eventParticipant", key = "#eventId + ':' + #userId", unless = "#result == null")
    public EventParticipant getParticipantByEventIdAndUserId(Long eventId, Long userId) {
        log.debug("이벤트 ID {}와 사용자 ID {}에 대한 참가자를 캐시에서 조회합니다.", eventId, userId);
        return eventParticipantRepository.findByEventIdAndUserId(eventId, userId).orElse(null);
    }

    /** 이벤트 ID와 사용자 ID로 참가자 존재 여부 확인 */
    @Cacheable(value = "eventParticipantExists", key = "#eventId + ':' + #userId")
    public boolean existsByEventIdAndUserId(Long eventId, Long userId) {
        log.debug("이벤트 ID {}와 사용자 ID {}에 대한 참가자 존재 여부를 캐시에서 확인합니다.", eventId, userId);
        return eventParticipantRepository.existsByEventIdAndUserId(eventId, userId);
    }

    /** 이벤트 참가자 저장 및 캐시 무효화 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventParticipantsByEvent", key = "#participant.event.id"),
            @CacheEvict(value = "eventParticipantsByUser", key = "#participant.user.id"),
            @CacheEvict(value = "eventParticipant", key = "#participant.event.id + ':' + #participant.user.id"),
            @CacheEvict(value = "eventParticipantExists", key = "#participant.event.id + ':' + #participant.user.id")
    })
    public EventParticipant saveEventParticipant(EventParticipant participant) {
        log.debug("이벤트 참가자 ID {}를 저장하고 관련 캐시를 무효화합니다.", participant.getId());
        return eventParticipantRepository.save(participant);
    }

    /** 이벤트 참가자 삭제 및 캐시 무효화 */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "eventParticipantsByEvent", key = "#participant.event.id"),
            @CacheEvict(value = "eventParticipantsByUser", key = "#participant.user.id"),
            @CacheEvict(value = "eventParticipant", key = "#participant.event.id + ':' + #participant.user.id"),
            @CacheEvict(value = "eventParticipantExists", key = "#participant.event.id + ':' + #participant.user.id")
    })
    public void deleteEventParticipant(EventParticipant participant) {
        log.debug("이벤트 참가자 ID {}를 삭제하고 관련 캐시를 무효화합니다.", participant.getId());
        eventParticipantRepository.delete(participant);
    }

    /** 모든 이벤트 참가자 캐시 삭제 */
    @CacheEvict(value = {
            "eventParticipantsByEvent", "eventParticipantsByUser", "eventParticipant", "eventParticipantExists"
    }, allEntries = true)
    public void evictAllEventParticipantCaches() {
        log.info("모든 이벤트 참가자 캐시가 삭제되었습니다.");
    }

    /** 이벤트 ID로 모든 참가자 삭제 및 캐시 무효화 */
    @Transactional
    public void deleteParticipantsByEventId(Long eventId) {
        List<EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);
        eventParticipantRepository.deleteAll(participants);
        evictAllEventParticipantCaches();
        log.info("이벤트 ID {}에 대한 모든 참가자가 삭제되었습니다.", eventId);
    }

}
