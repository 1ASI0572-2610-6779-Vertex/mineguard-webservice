package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRiskDriver;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRiskDriverResource;

public final class DashboardRiskDriverResourceFromEntityAssembler {
    private DashboardRiskDriverResourceFromEntityAssembler() {
    }

    public static DashboardRiskDriverResource toResourceFromEntity(DashboardRiskDriver d) {
        return new DashboardRiskDriverResource(d.getId(), d.getDriverId(), d.getDriverName(), d.getVehicleType(), d.getRiskScore());
    }
}
