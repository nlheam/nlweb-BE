package com.nlweb.event.service;

import com.nlweb.event.repository.EventParticipantRepository;
import com.nlweb.event.entity.EventParticipant;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventParticipantCacheService {

    private final EventParticipantRepository eventParticipantRepository;



}
