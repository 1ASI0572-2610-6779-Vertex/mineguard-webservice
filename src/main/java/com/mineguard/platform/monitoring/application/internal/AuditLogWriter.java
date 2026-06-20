package com.mineguard.platform.monitoring.application.internal;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogWriter {
    private final AuditLogEntryRepository auditLogEntryRepository;

    public AuditLogWriter(AuditLogEntryRepository auditLogEntryRepository) {
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    public void record(String category, String titleKey, String descriptionKey, String paramsJson, String actorKey) {
        auditLogEntryRepository.save(new AuditLogEntry(category, LocalDateTime.now().toString(),
                titleKey, descriptionKey, paramsJson == null ? "{}" : paramsJson,
                actorKey == null ? "monitoring.audit.actors.systemAutomatic" : actorKey));
    }
}
