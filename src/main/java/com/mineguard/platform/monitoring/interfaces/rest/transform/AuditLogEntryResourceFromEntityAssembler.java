package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AuditLogEntryResource;

import java.util.Map;

public final class AuditLogEntryResourceFromEntityAssembler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AuditLogEntryResourceFromEntityAssembler() {
    }

    public static AuditLogEntryResource toResourceFromEntity(AuditLogEntry e) {
        Map<String, Object> params = Map.of();
        if (e.getDescriptionParamsJson() != null && !e.getDescriptionParamsJson().isBlank()) {
            try {
                params = MAPPER.readValue(e.getDescriptionParamsJson(), new TypeReference<Map<String, Object>>() {
                });
            } catch (Exception ignored) {
                params = Map.of();
            }
        }
        return new AuditLogEntryResource(e.getId(), e.getCategory(), e.getOccurredAt(), e.getTitleKey(),
                e.getDescriptionKey(), params, e.getActorKey());
    }
}
