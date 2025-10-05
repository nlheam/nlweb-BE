package com.nlweb.admin.repository;

import com.nlweb.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /** 모든 관리자 조회 */
    @Query("SELECT a FROM Admin a ORDER BY a.createdAt")
    List<Admin> findAllAdmins();

    /** 사용자 ID로 관리자 조회 */
    Optional<Admin> findByUserId(Long userId);

    /** 학생 ID로 관리자 조회 */
    @Query("SELECT a FROM Admin a JOIN a.user u WHERE u.studentId =: studentId")
    Optional<Admin> findByStudentId(@Param("studentId") String studentId);

    /** 학생 ID로 관리자 존재 여부 확인 */
    @Query("SELECT COUNT(a) > 0 FROM Admin a JOIN a.user u WHERE u.studentId = :studentId")
    boolean existsByStudentId(@Param("studentId") String studentId);

    /** 사용자 ID로 관리자 존재 여부 확인 */
    boolean existsByUserId(Long userId);

    /** 관리자 역할로 존재 여부 확인 */
    boolean existsByRole(String role);

    /** 관리자 검색 (사용자명 포함) */
    @Query("SELECT a FROM Admin a JOIN FETCH a.user u WHERE " +
            "(a.role LIKE %:keyword% OR u.username LIKE %:keyword% OR u.studentId LIKE %:keyword%) " +
            "ORDER BY a.createdAt")
    List<Admin> searchAdminsWithUser(@Param("keyword") String keyword);

}
