package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.AuditLogEntryPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.AuditLogEntryPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuditLogEntryRepositoryImpl implements AuditLogEntryRepository {
    private final AuditLogEntryPersistenceRepository repository;

    public AuditLogEntryRepositoryImpl(AuditLogEntryPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLogEntry save(AuditLogEntry entry) {
        return AuditLogEntryPersistenceAssembler.toDomain(repository.save(AuditLogEntryPersistenceAssembler.toEntity(entry)));
    }

    @Override
    public List<AuditLogEntry> findAll() {
        return repository.findAll().stream().map(AuditLogEntryPersistenceAssembler::toDomain).toList();
    }
}
