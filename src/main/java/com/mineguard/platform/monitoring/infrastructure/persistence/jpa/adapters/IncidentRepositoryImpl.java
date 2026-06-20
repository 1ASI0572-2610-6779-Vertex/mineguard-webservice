package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.Incident;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.IncidentPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.IncidentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {
    private final IncidentPersistenceRepository repository;

    public IncidentRepositoryImpl(IncidentPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Incident save(Incident incident) {
        return IncidentPersistenceAssembler.toDomain(repository.save(IncidentPersistenceAssembler.toEntity(incident)));
    }

    @Override
    public List<Incident> findAll() {
        return repository.findAll().stream().map(IncidentPersistenceAssembler::toDomain).toList();
    }

    @Override
    public List<Incident> findAllByCompanyId(Long companyId) {
        return repository.findAllByCompanyId(companyId).stream().map(IncidentPersistenceAssembler::toDomain).toList();
    }
}
