package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.AlertPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlertRepositoryImpl implements AlertRepository {
    private final AlertPersistenceRepository repository;

    public AlertRepositoryImpl(AlertPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Alert save(Alert alert) {
        return AlertPersistenceAssembler.toDomain(repository.save(AlertPersistenceAssembler.toEntity(alert)));
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return repository.findById(id).map(AlertPersistenceAssembler::toDomain);
    }

    @Override
    public List<Alert> findAll() {
        return repository.findAll().stream().map(AlertPersistenceAssembler::toDomain).toList();
    }
}
