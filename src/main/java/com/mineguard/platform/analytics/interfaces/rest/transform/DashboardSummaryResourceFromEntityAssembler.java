package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardSummary;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardSummaryResource;

public final class DashboardSummaryResourceFromEntityAssembler {
    private DashboardSummaryResourceFromEntityAssembler() {
    }

    public static DashboardSummaryResource toResourceFromEntity(DashboardSummary d) {
        return new DashboardSummaryResource(d.getId(), d.getActiveSensors(), d.getTotalSensors(), d.getCriticalAlerts(), d.getFatigueEvents(), d.getActiveVehicles(), d.getTotalDrivers());
    }
}
