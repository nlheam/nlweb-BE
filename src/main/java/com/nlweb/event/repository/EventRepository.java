package com.nlweb.event.repository;

import com.nlweb.common.enums.EventType;
import com.nlweb.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /** 제목으로 이벤트 조회 */
    Optional<Event> findByTitle(String title);

    /** 이벤트 타입으로 이벤트 목록 조회 */
    List<Event> findEventsByEventType(EventType eventType);

    /** 활성화된 이벤트를 시작 날짜 기준 오름차순으로 조회 */
    List<Event> findByIsActiveTrueOrderByStartDateTimeAsc();

    /** 이벤트 타입별 이벤트를 시작 날짜 기준 오름차순으로 조회 */
    List<Event> findByEventTypeOrderByStartDateTimeAsc(EventType eventType);

    /** 상위 이벤트로 하위 이벤트 목록 조회 */
    List<Event> findByParentEvent(Event parentEvent);

    /** 참가자 ID로 이벤트 목록 조회 (참가자 기준) */
    @Query("SELECT e FROM Event e JOIN EventParticipant ep ON e.id = ep.event.id " +
           "WHERE ep.id = :id ORDER BY e.startDateTime")
    List<Event> findAllByEventParticipantId(Long id);

    /** 다가오는 이벤트 조회 */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.startDateTime) > DATE(:datetime) " +
            "AND e.isActive = true ORDER BY e.startDateTime")
    List<Event> findUpcomingEvents(@Param("datetime") LocalDateTime datetime);

    /** 현재 진행 중인 이벤트 조회 */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.startDateTime) <= DATE(:datetime) AND DATE(:datetime) <= DATE(e.endDateTime) " +
            "AND e.isActive = true ORDER BY e.startDateTime")
    List<Event> findOngoingEvents(@Param("datetime") LocalDateTime datetime);

    /** 기간이 끝난 이벤트 조회 */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.endDateTime) < DATE(:datetime) " +
            "AND e.isActive = true ORDER BY e.endDateTime DESC")
    List<Event> findPastEvents(@Param("datetime") LocalDateTime datetime);

}
