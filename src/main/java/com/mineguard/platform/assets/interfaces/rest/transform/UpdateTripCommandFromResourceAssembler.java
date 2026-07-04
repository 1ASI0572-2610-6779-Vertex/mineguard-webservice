package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.commands.UpdateTripCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateDrivingSessionResource;

public final class UpdateTripCommandFromResourceAssembler {
    private UpdateTripCommandFromResourceAssembler() {
    }

    public static UpdateTripCommand toCommandFromResource(Long sessionId, UpdateDrivingSessionResource resource) {
        var status = resource.status() == null ? null : TripStatus.fromSerialized(resource.status());
        return new UpdateTripCommand(sessionId, status);
    }
}
