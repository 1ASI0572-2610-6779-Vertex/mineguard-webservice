package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;

public final class AlertPersistenceAssembler {
    private AlertPersistenceAssembler() {
    }

    public static Alert toDomain(AlertPersistenceEntity e) {
        if (e == null) return null;
        var a = new Alert(e.getCode(), e.getType(), e.getPriority(), e.getStatus(), e.getOccurredAt(),
                e.getTitle(), e.getDescription(), e.getVehicleClassKey(), e.getVehicleCode(),
                e.getDriverName(), e.getResolutionNotes());
        a.setId(e.getId());
        return a;
    }

    public static AlertPersistenceEntity toEntity(Alert a) {
        var e = new AlertPersistenceEntity();
        e.setId(a.getId());
        e.setCode(a.getCode());
        e.setType(a.getType());
        e.setPriority(a.getPriority());
        e.setStatus(a.getStatus());
        e.setOccurredAt(a.getOccurredAt());
        e.setTitle(a.getTitle());
        e.setDescription(a.getDescription());
        e.setVehicleClassKey(a.getVehicleClassKey());
        e.setVehicleCode(a.getVehicleCode());
        e.setDriverName(a.getDriverName());
        e.setResolutionNotes(a.getResolutionNotes());
        return e;
    }
}
