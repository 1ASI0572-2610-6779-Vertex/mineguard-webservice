package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AuditLogEntryPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogEntryPersistenceRepository extends JpaRepository<AuditLogEntryPersistenceEntity, Long> {
    List<AuditLogEntryPersistenceEntity> findAllByCompanyId(Long companyId);
}
