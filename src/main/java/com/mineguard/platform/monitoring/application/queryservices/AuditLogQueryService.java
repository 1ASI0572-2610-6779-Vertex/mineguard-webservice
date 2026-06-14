package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.domain.model.queries.GetAuditLogQuery;

import java.util.List;

public interface AuditLogQueryService {
    List<AuditLogEntry> handle(GetAuditLogQuery query);
}
