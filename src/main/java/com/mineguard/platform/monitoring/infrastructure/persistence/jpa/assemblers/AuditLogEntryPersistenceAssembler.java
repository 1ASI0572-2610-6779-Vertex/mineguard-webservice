package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.AuditLogEntry;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AuditLogEntryPersistenceEntity;

public final class AuditLogEntryPersistenceAssembler {
    private AuditLogEntryPersistenceAssembler() {
    }

    public static AuditLogEntry toDomain(AuditLogEntryPersistenceEntity e) {
        if (e == null) return null;
        var a = new AuditLogEntry(e.getCategory(), e.getOccurredAt(), e.getTitleKey(), e.getDescriptionKey(),
                e.getDescriptionParamsJson(), e.getActorKey());
        a.setId(e.getId());
        return a;
    }

    public static AuditLogEntryPersistenceEntity toEntity(AuditLogEntry a) {
        var e = new AuditLogEntryPersistenceEntity();
        e.setId(a.getId());
        e.setCategory(a.getCategory());
        e.setOccurredAt(a.getOccurredAt());
        e.setTitleKey(a.getTitleKey());
        e.setDescriptionKey(a.getDescriptionKey());
        e.setDescriptionParamsJson(a.getDescriptionParamsJson());
        e.setActorKey(a.getActorKey());
        return e;
    }
}
