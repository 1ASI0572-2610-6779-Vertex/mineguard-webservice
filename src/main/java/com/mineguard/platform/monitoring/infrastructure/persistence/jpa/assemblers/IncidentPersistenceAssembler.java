package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.Incident;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;

public final class IncidentPersistenceAssembler {
    private IncidentPersistenceAssembler() {
    }

    public static Incident toDomain(IncidentPersistenceEntity e) {
        if (e == null) return null;
        var i = new Incident(e.getAlertId(), e.getDescription(), e.getIncidentDate(), e.getStatus(), e.getSeverity());
        i.setId(e.getId());
        return i;
    }

    public static IncidentPersistenceEntity toEntity(Incident i) {
        var e = new IncidentPersistenceEntity();
        e.setId(i.getId());
        e.setAlertId(i.getAlertId());
        e.setDescription(i.getDescription());
        e.setIncidentDate(i.getIncidentDate());
        e.setStatus(i.getStatus());
        e.setSeverity(i.getSeverity());
        return e;
    }
}
