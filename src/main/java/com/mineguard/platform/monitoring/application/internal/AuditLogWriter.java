package com.mineguard.platform.monitoring.application.internal;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogWriter {
    private final AuditLogEntryRepository auditLogEntryRepository;
    private final SecurityContextFacade securityContext;

    public AuditLogWriter(AuditLogEntryRepository auditLogEntryRepository, SecurityContextFacade securityContext) {
        this.auditLogEntryRepository = auditLogEntryRepository;
        this.securityContext = securityContext;
    }

    /** The company is always resolved from the active security context — never accepted as a parameter. */
    public void record(String category, String titleKey, String descriptionKey, String paramsJson, String actorKey) {
        auditLogEntryRepository.save(new AuditLogEntry(category, LocalDateTime.now().toString(),
                titleKey, descriptionKey, paramsJson == null ? "{}" : paramsJson,
                actorKey == null ? "monitoring.audit.actors.systemAutomatic" : actorKey,
                securityContext.currentCompanyId()));
    }
}
