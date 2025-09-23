package com.nlweb.repository;

import com.nlweb.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import com.nlweb.enums.EventType;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByTitle(String title);

    List<Event> findEventsByEventType(EventType eventType);

    List<Event> findByIsActiveTrueOrderByStartDateTimeAsc();

    List<Event> findByEventTypeOrderByStartDateTimeAsc(EventType eventType);

    /**
     * 다가오는 이벤트 조회
     */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.startDateTime) > DATE(:datetime) " +
            "AND e.isActive = true ORDER BY e.startDateTime")
    List<Event> findUpcomingEvents(@Param("date") LocalDateTime datetime);

    /**
     * 현재 진행 중인 이벤트 조회
     */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.startDateTime) <= DATE(:datetime) AND DATE(:datetime) <= DATE(e.endDateTime) " +
            "AND e.isActive = true ORDER BY e.startDateTime")
    List<Event> findOngoingEvents(@Param("date") LocalDateTime datetime);

    /**
     * 기간이 끝난 이벤트 조회
     */
    @Query("SELECT e FROM Event e " +
            "WHERE DATE(e.endDateTime) < DATE(:datetime) " +
            "AND e.isActive = true ORDER BY e.endDateTime DESC")
    List<Event> findPastEvents(@Param("date") LocalDateTime datetime);
}
