package com.nlweb.repository;

import com.nlweb.entity.EnsembleParticipant;

import com.nlweb.enums.EnsembleSessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnsembleParticipantRepository extends JpaRepository<EnsembleParticipant, Long> {

    List<EnsembleParticipant> findByEnsembleId(Long ensembleId);
    List<EnsembleParticipant> findByUserId(Long userId);
    List<EnsembleParticipant> findByEnsembleSessionType(EnsembleSessionType ensembleSessionType);

    boolean existsByEnsembleIdAndEnsembleSessionType(Long ensembleId, EnsembleSessionType ensembleSessionType);

    @Query("SELECT p.ensembleSessionType, COUNT(p) FROM EnsembleParticipant p GROUP BY p.ensembleSessionType")
    List<Object[]> getSessionStatistics();
}
