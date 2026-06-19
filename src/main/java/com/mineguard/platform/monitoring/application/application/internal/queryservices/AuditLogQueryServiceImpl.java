package com.mineguard.platform.monitoring.application.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.AuditLogQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogQueryServiceImpl implements AuditLogQueryService {
    private final AuditLogEntryRepository repository;

    public AuditLogQueryServiceImpl(AuditLogEntryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AuditLogEntry> handle(GetAuditLogQuery query) {
        return repository.findAll();
    }
}
