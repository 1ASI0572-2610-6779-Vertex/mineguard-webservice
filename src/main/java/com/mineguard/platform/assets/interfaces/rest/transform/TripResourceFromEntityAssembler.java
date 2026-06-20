package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.interfaces.rest.resources.TripResource;

public final class TripResourceFromEntityAssembler {
    private TripResourceFromEntityAssembler() {
    }

    public static TripResource toResourceFromEntity(Trip t) {
        return new TripResource(t.getId(), t.getDriverId(), t.getVehicleId(),
                t.getStartTime(), t.getEndTime(),
                t.getStatus() != null ? t.getStatus().toSerialized() : null);
    }
}
