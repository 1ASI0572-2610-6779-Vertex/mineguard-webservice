package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.interfaces.rest.resources.CardiacReadingResource;

public final class CardiacReadingResourceFromEntityAssembler {
    private CardiacReadingResourceFromEntityAssembler() {
    }

    public static CardiacReadingResource toResourceFromEntity(CardiacReading r) {
        return new CardiacReadingResource(r.getId(), r.getDriverName(), r.getVehicleCode(), r.getHeartRate(), r.getStatus());
    }
}
