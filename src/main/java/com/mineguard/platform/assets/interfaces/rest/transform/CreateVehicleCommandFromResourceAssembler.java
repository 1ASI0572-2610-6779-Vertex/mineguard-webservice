package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.commands.CreateVehicleCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateVehicleResource;

public final class CreateVehicleCommandFromResourceAssembler {
    private CreateVehicleCommandFromResourceAssembler() {
    }

    public static CreateVehicleCommand toCommandFromResource(CreateVehicleResource r) {
        VehicleStatus status = r.status() == null ? VehicleStatus.OPERATIONAL : VehicleStatus.fromSerialized(r.status());
        return new CreateVehicleCommand(r.code(), r.model(), r.category(), status,
                r.assignedDriverName(), r.shiftLabel());
    }
}
