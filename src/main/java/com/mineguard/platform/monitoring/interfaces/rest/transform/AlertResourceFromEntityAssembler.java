package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertResource;

public final class AlertResourceFromEntityAssembler {
    private AlertResourceFromEntityAssembler() {
    }

    public static AlertResource toResourceFromEntity(Alert a) {
        return new AlertResource(a.getId(), a.getCode(), a.getType().toSerialized(), a.getPriority().toSerialized(),
                a.getStatus().toSerialized(), a.getOccurredAt(), a.getTitle(), a.getDescription(),
                a.getVehicleClassKey(), a.getVehicleCode(), a.getDriverName(), a.getResolutionNotes());
    }
}
