package com.nlweb.repository;

import com.nlweb.entity.Timeslot;
import com.nlweb.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 타임슬롯 리포지토리
 */
@Repository
public interface TimeslotRepository extends JpaRepository<Timeslot, Long> {

    // ========== 기본 조회 ==========

    /**
     * 활성 타임슬롯 조회
     */
    List<Timeslot> findByIsActiveTrueOrderByDayOfWeekAscStartTimeAsc();

    /**
     * 요일별 타임슬롯 조회
     */
    List<Timeslot> findByDayOfWeekAndIsActiveTrueOrderByStartTime(DayOfWeek dayOfWeek);

    /**
     * 앙상블별 타임슬롯 조회
     */
    List<Timeslot> findByEnsembleIdAndIsActiveTrue(Long ensembleId);

    /**
     * 이벤트별 타임슬롯 조회
     */
    List<Timeslot> findByEventIdAndIsActiveTrue(Long eventId);

    // ========== 동시성 제어 ==========

    /**
     * 락을 걸고 타임슬롯 조회 (동시성 제어용)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Timeslot t WHERE t.id = :id")
    Optional<Timeslot> findByIdForUpdate(@Param("id") Long id);

    // ========== 시간 기반 조회 ==========

    /**
     * 특정 시간 범위 내의 타임슬롯 조회
     */
    @Query("SELECT t FROM Timeslot t WHERE t.dayOfWeek = :dayOfWeek AND " +
            "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
            "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
            "(t.startTime >= :startTime AND t.endTime <= :endTime)) AND " +
            "t.isActive = true")
    List<Timeslot> findOverlappingTimeslots(@Param("dayOfWeek") DayOfWeek dayOfWeek,
                                            @Param("startTime") LocalTime startTime,
                                            @Param("endTime") LocalTime endTime);

    /**
     * 특정 시간대에 사용 가능한 타임슬롯 조회 (충돌 없는 것들)
     */
    @Query("SELECT t FROM Timeslot t WHERE t.dayOfWeek = :dayOfWeek AND " +
            "NOT ((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
            "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
            "(t.startTime >= :startTime AND t.endTime <= :endTime)) AND " +
            "t.isActive = true")
    List<Timeslot> findNonOverlappingTimeslots(@Param("dayOfWeek") DayOfWeek dayOfWeek,
                                               @Param("startTime") LocalTime startTime,
                                               @Param("endTime") LocalTime endTime);

    // ========== 제외 날짜 관련 ==========

    /**
     * 특정 날짜가 제외 날짜에 포함된 타임슬롯 조회 (PostgreSQL 배열 연산)
     */
    @Query(value = "SELECT * FROM timeslots WHERE :date = ANY(excluded_dates) AND is_active = true", nativeQuery = true)
    List<Timeslot> findTimeslotsExcludingDate(@Param("date") LocalDate date);

    /**
     * 특정 날짜가 제외 날짜에 포함되지 않은 타임슬롯 조회
     */
    @Query(value = "SELECT * FROM timeslots WHERE " +
            "day_of_week = CAST(:dayOfWeek AS text) AND " +
            "(:date != ALL(excluded_dates) OR excluded_dates IS NULL) AND " +
            "is_active = true " +
            "ORDER BY start_time", nativeQuery = true)
    List<Timeslot> findAvailableTimeslotsOnDate(@Param("dayOfWeek") String dayOfWeek,
                                                @Param("date") LocalDate date);

}
