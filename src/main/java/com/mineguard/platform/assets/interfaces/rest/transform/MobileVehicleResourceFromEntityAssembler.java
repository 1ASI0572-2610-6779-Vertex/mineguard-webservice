package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.assets.interfaces.rest.resources.MobileVehicleResource;

/** Projects a Vehicle onto the mobile selection contract (status: available|inUse|maintenance). */
public final class MobileVehicleResourceFromEntityAssembler {
    private MobileVehicleResourceFromEntityAssembler() {
    }

    public static MobileVehicleResource toResourceFromEntity(Vehicle v) {
        String status;
        if (v.getStatus() == VehicleStatus.MAINTENANCE) {
            status = "maintenance";
        } else if (v.isAssigned()) {
            status = "inUse";
        } else {
            status = "available";
        }
        String name = v.getModel() != null && !v.getModel().isBlank() ? v.getModel() : v.getCode();
        return new MobileVehicleResource(String.valueOf(v.getId()), name, v.getCategory(), status);
    }
}
