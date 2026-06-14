package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.interfaces.rest.resources.DriverResource;

public final class DriverResourceFromEntityAssembler {
    private DriverResourceFromEntityAssembler() {
    }

    public static DriverResource toResourceFromEntity(Driver d) {
        return new DriverResource(d.getId(), d.getFullName(), d.getOperatorId(), d.getLicense(),
                d.getSpecialty(), d.getShiftStatus().toSerialized(), d.getLastAccess());
    }
}
