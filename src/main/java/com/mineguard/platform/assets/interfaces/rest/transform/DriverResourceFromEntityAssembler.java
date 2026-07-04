package com.mineguard.platform.assets.interfaces.rest.transform;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.interfaces.rest.resources.DriverResource;

public final class DriverResourceFromEntityAssembler {
    private DriverResourceFromEntityAssembler() {
    }

    public static DriverResource toResourceFromEntity(Driver d) {
        return toResourceFromEntity(d, null);
    }

    /** @param riskScore populated only when the caller has it on hand (e.g. {@code ?sort=-riskScore}); null otherwise. */
    public static DriverResource toResourceFromEntity(Driver d, Double riskScore) {
        return new DriverResource(d.getId(), d.getFullName(), d.getOperatorId(), d.getLicense(),
                d.getSpecialty(), d.getShiftStatus().toSerialized(), d.getLastAccess(), riskScore);
    }
}
