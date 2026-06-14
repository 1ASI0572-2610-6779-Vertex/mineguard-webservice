package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;

import java.util.List;

public interface AuditLogEntryRepository {
    AuditLogEntry save(AuditLogEntry entry);
    List<AuditLogEntry> findAll();
}
