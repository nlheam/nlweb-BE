package com.nlweb.ensemble.repository;

import com.nlweb.ensemble.entity.Ensemble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EnsembleRepository extends JpaRepository<Ensemble, Long> {

    /** 합주 ID로 합주 조회 */
    Optional<Ensemble> findEnsembleById(Long id);

    /** 활성화 합주 목록 조회 */
    List<Ensemble> findByIsActiveTrue();

    /** 특정 user가 속한 모든 합주 목록 조회 */
    @Query("SELECT e FROM Ensemble e JOIN e.members m WHERE m.user.id = :userId")
    List<Ensemble> findByMemberUserId(@Param("userId") Long userId);

    /** 아티스트 명으로 합주 목록 조회 */
    List<Ensemble> findByArtistContainingIgnoreCase(String artist);

    /** 제목으로 합주 목록 조회 */
    List<Ensemble> findByTitleContainingIgnoreCase(String title);

    /** 합주 검색 */
    @Query("SELECT e FROM Ensemble e WHERE (e.artist LIKE %:keyword% OR e.title LIKE %:keyword%) ORDER BY e.createdAt DESC")
    Page<Ensemble> searchEnsembles(@Param("keyword") String keyword, Pageable pageable);

}
