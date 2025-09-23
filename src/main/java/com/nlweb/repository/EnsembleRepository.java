package com.nlweb.repository;

import com.nlweb.entity.Ensemble;
import com.nlweb.entity.EnsembleParticipant;
import com.nlweb.entity.User;
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

    Optional<Ensemble> findById(long id);

    List<Ensemble> findByIsActiveTrueOrderByCreatedAtDesc();

    List<Ensemble> findByEventIdAndIsActiveTrueOrderByCreatedAtAsc(Long eventId);

    List<Ensemble> findByArtistContainingIgnoreCaseAndIsActiveTrue(String artist);

    List<Ensemble> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    @Query("SELECT e FROM Ensemble e WHERE (e.artist LIKE %:keyword% OR e.title LIKE %:keyword%) AND e.isActive = true ORDER BY e.createdAt DESC")
    Page<Ensemble> searchEnsembles(@Param("keyword") String keyword, Pageable pageable);
}
