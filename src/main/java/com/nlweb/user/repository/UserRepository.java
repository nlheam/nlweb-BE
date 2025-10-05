package com.nlweb.user.repository;

import com.nlweb.user.entity.User;
import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== 기본 조회 ==========

    Optional<User> findByStudentId(String studentId);

    List<User> findAllByStudentIdIn(List<String> studentIds);

    List<User> findAllByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    List<User> findBySessionAndStatus(UserSessionType session, UserStatus status);

    @Query("SELECT u FROM User u WHERE u.studentId = :identifier OR u.email = :identifier")
    Optional<User> findByStudentIdOrEmail(@Param("identifier") String identifier);

    List<User> findByStatus(UserStatus status);

    Page<User> findByStatus(UserStatus status, Pageable pageable);

    List<User> findByStatusOrderByCreatedAtAsc(UserStatus status);

    List<User> findByBatchAndStatusOrderByBatchAsc(Integer batch, UserStatus status);

    /**
     * 세션별 사용자 수 조회
     */
    @Query("SELECT u.session, COUNT(u) FROM User u WHERE u.status = :status GROUP BY u.session")
    List<Object[]> countBySessionAndStatus(@Param("status") UserStatus status);


    /**
     * 기수별 사용자 수 조회
     */
    @Query("SELECT u.batch, COUNT(u) FROM User u WHERE u.status = :status GROUP BY u.batch ORDER BY u.batch ASC")
    List<Object[]> countByBatchAndStatus(@Param("status") UserStatus status);

    /**
     * 기수별 사용자 조회
     */
    List<User> findByBatchAndStatusOrderByStudentIdAsc(Integer batch, UserStatus status);

    /**
     * 기수 범위별 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.batch BETWEEN :startBatch AND :endBatch AND u.status = :status ORDER BY u.batch, u.studentId")
    List<User> findByBatchRangeAndStatus(@Param("startBatch") Integer startBatch,
                                         @Param("endBatch") Integer endBatch,
                                         @Param("status") UserStatus status);


    // ========== 통계 조회 ==========

    /**
     * 전체 사용자 수 조회
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'ACTIVE'")
    Long countActiveUsers();

    /**
     * 상태별 사용자 수 조회
     */
    Long countByStatus(UserStatus status);

    // ========== 검색 ==========

    /**
     * 이름으로 검색 (부분 일치)
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% AND u.status = :status")
    Page<User> searchByUsernameContaining(@Param("keyword") String keyword,
                                          @Param("status") UserStatus status,
                                          Pageable pageable);

    /**
     * 복합 검색 (학번, 이름, 이메일)
     */
    @Query("SELECT u FROM User u WHERE (u.studentId LIKE %:keyword% OR u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.status = :status")
    Page<User> searchUsers(@Param("keyword") String keyword,
                           @Param("status") UserStatus status,
                           Pageable pageable);

    // ========== 중복 확인 ==========

    /**
     * 학번 중복 확인
     */
    boolean existsByStudentId(String studentId);

    /**
     * 이메일 중복 확인
     */
    boolean existsByEmail(String email);

    /**
     * 전화번호 중복 확인
     */
    boolean existsByPhone(String phone);
}
