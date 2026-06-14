package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.SensorReadingPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.SensorReadingPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SensorReadingRepositoryImpl implements SensorReadingRepository {
    private final SensorReadingPersistenceRepository repository;

    public SensorReadingRepositoryImpl(SensorReadingPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public SensorReading save(SensorReading reading) {
        return SensorReadingPersistenceAssembler.toDomain(repository.save(SensorReadingPersistenceAssembler.toEntity(reading)));
    }

    @Override
    public List<SensorReading> findAll() {
        return repository.findAll().stream().map(SensorReadingPersistenceAssembler::toDomain).toList();
    }
}
