package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.interfaces.rest.resources.VehicleResource;

public final class VehicleResourceFromEntityAssembler {
    private VehicleResourceFromEntityAssembler() {
    }

    public static VehicleResource toResourceFromEntity(Vehicle v) {
        return toResourceFromEntity(v, null);
    }

    /**
     * @param deviceId the linked device's id (as a string), or {@code null} when the vehicle has no
     *                 device. Populated by the inventory endpoint from the tenant's sensor collection.
     */
    public static VehicleResource toResourceFromEntity(Vehicle v, String deviceId) {
        return new VehicleResource(v.getId(), v.getCode(), v.getModel(), v.getCategory(),
                v.getStatus().toSerialized(), v.getAssignedDriverName(), v.getShiftLabel(), deviceId);
    }
}
