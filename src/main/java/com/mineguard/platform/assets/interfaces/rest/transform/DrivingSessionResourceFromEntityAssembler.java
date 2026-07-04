package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.interfaces.rest.resources.DrivingSessionResource;

public final class DrivingSessionResourceFromEntityAssembler {
    private DrivingSessionResourceFromEntityAssembler() {
    }

    public static DrivingSessionResource toResourceFromEntity(Trip t) {
        return new DrivingSessionResource(t.getId(), t.getDriverId(), t.getVehicleId(),
                t.getStartTime(), t.getEndTime(),
                t.getStatus() != null ? t.getStatus().toSerialized() : null);
    }
}
