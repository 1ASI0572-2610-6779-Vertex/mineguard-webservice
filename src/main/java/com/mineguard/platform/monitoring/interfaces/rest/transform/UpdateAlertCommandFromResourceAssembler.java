package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.interfaces.rest.resources.UpdateAlertResource;

public final class UpdateAlertCommandFromResourceAssembler {
    private UpdateAlertCommandFromResourceAssembler() {
    }

    public static UpdateAlertCommand toCommandFromResource(Long id, UpdateAlertResource r) {
        AlertType type = r.type() == null ? null : AlertType.fromSerialized(r.type());
        AlertPriority priority = r.priority() == null ? null : AlertPriority.fromSerialized(r.priority());
        AlertStatus status = r.status() == null ? null : AlertStatus.fromSerialized(r.status());
        return new UpdateAlertCommand(id, type, priority, status, r.title(), r.description(),
                r.vehicleClassKey(), r.vehicleCode(), r.driverName(), r.resolutionNotes());
    }
}
