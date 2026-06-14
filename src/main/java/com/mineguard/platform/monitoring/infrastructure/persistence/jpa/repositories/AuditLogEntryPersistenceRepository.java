package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AuditLogEntryPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogEntryPersistenceRepository extends JpaRepository<AuditLogEntryPersistenceEntity, Long> {
}
