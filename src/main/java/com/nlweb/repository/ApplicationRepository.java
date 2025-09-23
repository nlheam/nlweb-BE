package com.nlweb.repository;

import com.nlweb.entity.Application;
import com.nlweb.enums.ApplicationStatus;
import com.nlweb.enums.ApplicationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // ========== 사용자별 조회 ==========

    /**
     * 사용자별 모든 신청 조회 (페이징)
     */
    Page<Application> findByUserStudentIdOrderByCreatedAtDesc(String studentId, Pageable pageable);


     /**
      * 사용자별 상태별 신청 조회
      */
    List<Application> findByUserStudentIdAndApplicationStatusOrderByCreatedAtDesc(String studentId, ApplicationStatus status);

    // ========== 상태별 조회 ==========

    /**
     * 상태별 신청 조회
     */
    List<Application> findByApplicationStatusOrderByCreatedAtDesc(ApplicationStatus status);

    // ========== 타입별 조회 ==========

    /**
     * 신청 타입별 조회
     */
    List<Application> findByApplicationTypeOrderByCreatedAtDesc(ApplicationType applicationType);

    /**
     * 타입과 상태별 조회
     */
    List<Application> findByApplicationTypeAndApplicationStatusOrderByCreatedAtDesc(ApplicationType applicationType, ApplicationStatus status);

    // ========== 대상별 조회 ==========

    /**
     * 앙상블별 신청 조회
     */
    List<Application> findByEnsembleIdOrderByCreatedAtDesc(Long ensembleId);

    /**
     * 타임슬롯별 신청 조회
     */
    List<Application> findByTimeslotIdOrderByCreatedAtDesc(Long timeslotId);

    /**
     * 이벤트별 신청 조회
     */
    List<Application> findByEventIdOrderByCreatedAtDesc(Long eventId);

    // ========== 통계 조회 ==========

    /**
     * 상태별 신청 수 조회
     */
    @Query("SELECT a.applicationStatus, COUNT(a) FROM Application a GROUP BY a.applicationStatus")
    List<Object[]> countByStatus();

    /**
     * 타입별 신청 수 조회
     */
    @Query("SELECT a.applicationType, COUNT(a) FROM Application a GROUP BY a.applicationType")
    List<Object[]> countByApplicationType();

    /**
     * 특정 기간 이후 상태별 신청 수
     */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.applicationStatus = :status AND a.createdAt > :after")
    long countByStatusAfter(@Param("status") ApplicationStatus status, @Param("after") LocalDateTime after);

    /**
     * 오늘의 신청 통계
     */
    @Query("SELECT a.applicationStatus, COUNT(a) FROM Application a WHERE DATE(a.createdAt) = DATE(:date) GROUP BY a.applicationStatus")
    List<Object[]> getTodayApplicationStats(@Param("date") LocalDateTime date);

    /**
     * 시간대별 신청 패턴
     */
    @Query("SELECT HOUR(a.createdAt), COUNT(a) FROM Application a WHERE a.createdAt >= :startDate GROUP BY HOUR(a.createdAt) ORDER BY HOUR(a.createdAt)")
    List<Object[]> getHourlyApplicationPattern(@Param("startDate") LocalDateTime startDate);

    /**
     * 사용자별 신청 성공률
     */
    @Query("SELECT a.user.studentId, a.user.username, " +
            "COUNT(a) as totalApplications, " +
            "SUM(CASE WHEN a.applicationStatus = 'SUCCESS' THEN 1 ELSE 0 END) as successfulApplications, " +
            "(SUM(CASE WHEN a.applicationStatus = 'SUCCESS' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(a)) as successRate " +
            "FROM Application a GROUP BY a.user.studentId, a.user.username " +
            "HAVING COUNT(a) >= :minApplications ORDER BY (SUM(CASE WHEN a.applicationStatus = 'SUCCESS' THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(a)) DESC")
    List<Object[]> getUserApplicationSuccessRates(@Param("minApplications") long minApplications);

    // ========== 시간 기반 조회 ==========

    /**
     * 특정 기간의 신청들
     */
    @Query("SELECT a FROM Application a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    List<Application> findApplicationsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * 오래된 신청들 (정리 대상)
     */
    List<Application> findByCreatedAtBeforeOrderByCreatedAtAsc(LocalDateTime before);

    // ========== 업데이트 쿼리 ==========

    /**
     * 오래된 신청들 일괄 삭제
     */
    @Modifying
    @Query("DELETE FROM Application a WHERE a.createdAt < :cutoffDate")
    int deleteOldApplications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * 특정 상태의 오래된 신청들 상태 변경
     */
    @Modifying
    @Query("UPDATE Application a SET a.applicationStatus = :newStatus WHERE a.applicationStatus = :oldStatus AND a.createdAt < :cutoffDate")
    int updateOldApplicationStatus(@Param("oldStatus") ApplicationStatus oldStatus,
                                   @Param("newStatus") ApplicationStatus newStatus,
                                   @Param("cutoffDate") LocalDateTime cutoffDate);
}
