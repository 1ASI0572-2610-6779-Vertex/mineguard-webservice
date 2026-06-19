package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertResource;

public final class AlertResourceFromEntityAssembler {
    private AlertResourceFromEntityAssembler() {
    }

    public static AlertResource toResourceFromEntity(Alert a) {
        var type = a.getRawType() != null && !a.getRawType().isBlank() ? a.getRawType() : a.getType().toSerialized();
        var priority = a.getSeverity() != null && !a.getSeverity().isBlank() ? a.getSeverity() : a.getPriority().toSerialized();
        return new AlertResource(a.getId(), a.getCode(), type, priority,
                a.getStatus().toSerialized(), a.getOccurredAt(), a.getTitle(), a.getDescription(),
                a.getVehicleClassKey(), a.getVehicleCode(), a.getDriverName(), a.getResolutionNotes());
    }
}
