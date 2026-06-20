package com.mineguard.platform.monitoring.interfaces.rest.resources;

import java.util.Map;

public record AuditLogEntryResource(Long id, String category, String occurredAt, String titleKey,
                                    String descriptionKey, Map<String, Object> descriptionParams, String actorKey) {
}
