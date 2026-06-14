package com.mineguard.platform.assets.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers.VehiclePersistenceAssembler;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories.VehiclePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VehicleRepositoryImpl implements VehicleRepository {
    private final VehiclePersistenceRepository repository;

    public VehicleRepositoryImpl(VehiclePersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        return VehiclePersistenceAssembler.toDomain(repository.save(VehiclePersistenceAssembler.toEntity(vehicle)));
    }

    @Override
    public Optional<Vehicle> findById(Long id) {
        return repository.findById(id).map(VehiclePersistenceAssembler::toDomain);
    }

    @Override
    public List<Vehicle> findAll() {
        return repository.findAll().stream().map(VehiclePersistenceAssembler::toDomain).toList();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long countByStatus(VehicleStatus status) {
        return repository.countByStatus(status);
    }
}
