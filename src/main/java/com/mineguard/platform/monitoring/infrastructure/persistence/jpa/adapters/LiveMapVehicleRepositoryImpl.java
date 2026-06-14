package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.repositories.LiveMapVehicleRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.LiveMapVehiclePersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.LiveMapVehiclePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LiveMapVehicleRepositoryImpl implements LiveMapVehicleRepository {
    private final LiveMapVehiclePersistenceRepository repository;

    public LiveMapVehicleRepositoryImpl(LiveMapVehiclePersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public LiveMapVehicle save(LiveMapVehicle vehicle) {
        return LiveMapVehiclePersistenceAssembler.toDomain(repository.save(LiveMapVehiclePersistenceAssembler.toEntity(vehicle)));
    }

    @Override
    public List<LiveMapVehicle> findAll() {
        return repository.findAll().stream().map(LiveMapVehiclePersistenceAssembler::toDomain).toList();
    }
}
