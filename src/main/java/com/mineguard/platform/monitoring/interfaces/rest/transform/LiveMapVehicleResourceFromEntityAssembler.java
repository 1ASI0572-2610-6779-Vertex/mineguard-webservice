package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.interfaces.rest.resources.LiveMapVehicleResource;

public final class LiveMapVehicleResourceFromEntityAssembler {
    private LiveMapVehicleResourceFromEntityAssembler() {
    }

    public static LiveMapVehicleResource toResourceFromEntity(LiveMapVehicle v) {
        return new LiveMapVehicleResource(v.getId(), v.getCode(), v.getVehicleType(), v.getLatitude(),
                v.getLongitude(), v.getStatus(), v.getDriverName());
    }
}
