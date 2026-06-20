package com.mineguard.platform.monitoring.interfaces.rest.transform;

import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.interfaces.rest.resources.FleetSummaryResource;

public final class FleetSummaryResourceFromEntityAssembler {
    private FleetSummaryResourceFromEntityAssembler() {
    }

    public static FleetSummaryResource toResourceFromEntity(FleetSummary s) {
        return new FleetSummaryResource(s.getId(), s.getOperational(), s.getMaintenance(), s.getAlert(),
                s.getTotal(), s.getOperationalPercent());
    }
}
