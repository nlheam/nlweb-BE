package com.nlweb.event.repository;

import com.nlweb.event.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Integer> {

    /** 이벤트 ID로 참가자 목록 조회 */
    List<EventParticipant> findByEventId(Long eventId);

    /** 사용자 ID로 참가자 목록 조회 */
    List<EventParticipant> findByUserId(Long userId);

    /** 이벤트 ID와 사용자 ID로 참가자 존재 여부 확인 */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

}
