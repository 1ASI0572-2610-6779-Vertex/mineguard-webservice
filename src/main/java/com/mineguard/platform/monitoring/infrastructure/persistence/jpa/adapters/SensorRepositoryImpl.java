package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.SensorPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.SensorPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SensorRepositoryImpl implements SensorRepository {
    private final SensorPersistenceRepository repository;

    public SensorRepositoryImpl(SensorPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Sensor save(Sensor sensor) {
        return SensorPersistenceAssembler.toDomain(repository.save(SensorPersistenceAssembler.toEntity(sensor)));
    }

    @Override
    public List<Sensor> findAll() {
        return repository.findAll().stream().map(SensorPersistenceAssembler::toDomain).toList();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Optional<Sensor> findByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId).map(SensorPersistenceAssembler::toDomain);
    }
}
