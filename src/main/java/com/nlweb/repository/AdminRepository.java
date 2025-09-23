package com.nlweb.repository;

import com.nlweb.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a ORDER BY a.createdAt")
    List<Admin> findAllAdmins();

    Optional<Admin> findByUserId(Long userId);

    @Query("SELECT a FROM Admin a JOIN a.user u WHERE u.studentId =: studentId")
    Optional<Admin> findByStudentId(@Param("studentId") String studentId);

    @Query("SELECT COUNT(a) > 0 FROM Admin a JOIN a.user u WHERE u.studentId = :studentId")
    boolean existsByStudentId(@Param("studentId") String studentId);

    boolean existsByUserId(Long userId);

    boolean existsByRole(String role);

    /**
     * 관리자 검색 (사용자명 포함)
     */
    @Query("SELECT a FROM Admin a JOIN FETCH a.user u WHERE " +
            "(a.role LIKE %:keyword% OR u.username LIKE %:keyword% OR u.studentId LIKE %:keyword%) " +
            "ORDER BY a.createdAt")
    List<Admin> searchAdminsWithUser(@Param("keyword") String keyword);
}
