package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.interfaces.rest.resources.VehicleResource;

public final class VehicleResourceFromEntityAssembler {
    private VehicleResourceFromEntityAssembler() {
    }

    public static VehicleResource toResourceFromEntity(Vehicle v) {
        return new VehicleResource(v.getId(), v.getCode(), v.getModel(), v.getCategory(),
                v.getStatus().toSerialized(), v.getAssignedDriverName(), v.getShiftLabel());
    }
}
