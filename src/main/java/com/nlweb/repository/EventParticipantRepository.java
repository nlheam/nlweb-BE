package com.nlweb.repository;

import com.nlweb.entity.Ensemble;
import com.nlweb.entity.EnsembleParticipant;
import com.nlweb.entity.EventParticipant;
import com.nlweb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    Optional<List<EventParticipant>> findByEventId(Long eventId);

    Optional<List<EventParticipant>> findByUserId(Long userId);

    boolean existsByEventId(Long eventId);

    boolean existsByUserId(Long userId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

}
