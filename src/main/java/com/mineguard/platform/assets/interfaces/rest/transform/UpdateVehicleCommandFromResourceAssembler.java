package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.commands.UpdateVehicleCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateVehicleResource;

public final class UpdateVehicleCommandFromResourceAssembler {
    private UpdateVehicleCommandFromResourceAssembler() {
    }

    public static UpdateVehicleCommand toCommandFromResource(Long id, UpdateVehicleResource r) {
        VehicleStatus status = r.status() == null ? null : VehicleStatus.fromSerialized(r.status());
        return new UpdateVehicleCommand(id, r.code(), r.model(), r.category(), status,
                r.assignedDriverName(), r.shiftLabel());
    }
}
