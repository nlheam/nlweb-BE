package com.nlweb.ensemble.service;

import com.nlweb.ensemble.entity.Ensemble;
import com.nlweb.ensemble.repository.EnsembleRepository;
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
public class EnsembleCacheService {

    private final EnsembleRepository ensembleRepository;

    @Cacheable(value = "ensembles:all")
    public List<Ensemble> getAllEnsembles() {
        return ensembleRepository.findAll();
    }



}
