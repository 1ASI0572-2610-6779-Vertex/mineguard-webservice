package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;
import com.mineguard.platform.monitoring.domain.repositories.HeartRateReadingRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.HeartRateReadingPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.HeartRateReadingPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HeartRateReadingRepositoryImpl implements HeartRateReadingRepository {
    private final HeartRateReadingPersistenceRepository repository;

    public HeartRateReadingRepositoryImpl(HeartRateReadingPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public HeartRateReading save(HeartRateReading reading) {
        return HeartRateReadingPersistenceAssembler.toDomain(repository.save(HeartRateReadingPersistenceAssembler.toEntity(reading)));
    }

    @Override
    public List<HeartRateReading> findAll() {
        return repository.findAll().stream().map(HeartRateReadingPersistenceAssembler::toDomain).toList();
    }
}
