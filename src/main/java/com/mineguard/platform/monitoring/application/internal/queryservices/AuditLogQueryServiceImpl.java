package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.AuditLogQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogQueryServiceImpl implements AuditLogQueryService {
    private final AuditLogEntryRepository repository;
    private final SecurityContextFacade securityContext;

    public AuditLogQueryServiceImpl(AuditLogEntryRepository repository, SecurityContextFacade securityContext) {
        this.repository = repository;
        this.securityContext = securityContext;
    }

    @Override
    public List<AuditLogEntry> handle(GetAuditLogQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        return repository.findAllByCompanyId(companyId);
    }
}
