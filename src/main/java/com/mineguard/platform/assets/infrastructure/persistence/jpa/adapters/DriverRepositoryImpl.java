package com.mineguard.platform.assets.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers.DriverPersistenceAssembler;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories.DriverPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DriverRepositoryImpl implements DriverRepository {
    private final DriverPersistenceRepository repository;

    public DriverRepositoryImpl(DriverPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Driver save(Driver driver) {
        return DriverPersistenceAssembler.toDomain(repository.save(DriverPersistenceAssembler.toEntity(driver)));
    }

    @Override
    public Optional<Driver> findById(Long id) {
        return repository.findById(id).map(DriverPersistenceAssembler::toDomain);
    }

    @Override
    public List<Driver> findAll() {
        return repository.findAll().stream().map(DriverPersistenceAssembler::toDomain).toList();
    }

    @Override
    public List<Driver> findAllByCompanyId(Long companyId) {
        return repository.findAllByCompanyId(companyId).stream().map(DriverPersistenceAssembler::toDomain).toList();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public long countByShiftStatus(ShiftStatus status) {
        return repository.countByShiftStatus(status);
    }

    @Override
    public Optional<Driver> findByUserId(Long userId) {
        return repository.findByUserId(userId).map(DriverPersistenceAssembler::toDomain);
    }
}
