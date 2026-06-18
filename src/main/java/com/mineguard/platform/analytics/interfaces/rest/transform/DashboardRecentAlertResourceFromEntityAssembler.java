package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRecentAlert;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRecentAlertResource;

public final class DashboardRecentAlertResourceFromEntityAssembler {
    private DashboardRecentAlertResourceFromEntityAssembler() {
    }

    public static DashboardRecentAlertResource toResourceFromEntity(DashboardRecentAlert d) {
        return new DashboardRecentAlertResource(d.getId(), d.getAlertCode(), d.getSeverity(), d.getCategory(), d.getDriverName(), d.getVehicleCode(), d.getVehicleType(), d.getRoute(), d.getTime(), d.getStatus());
    }
}
