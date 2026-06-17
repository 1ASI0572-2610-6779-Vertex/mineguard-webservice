package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardTrend;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardTrendResource;

public final class DashboardTrendResourceFromEntityAssembler {
    private DashboardTrendResourceFromEntityAssembler() {
    }

    public static DashboardTrendResource toResourceFromEntity(DashboardTrend d) {
        return new DashboardTrendResource(d.getId(), d.getHour(), d.getAlerts(), d.getIncidents());
    }
}
