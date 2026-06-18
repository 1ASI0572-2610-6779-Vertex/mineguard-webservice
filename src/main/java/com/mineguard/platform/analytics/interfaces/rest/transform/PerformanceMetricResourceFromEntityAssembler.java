package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.interfaces.rest.resources.PerformanceMetricResource;

public final class PerformanceMetricResourceFromEntityAssembler {
    private PerformanceMetricResourceFromEntityAssembler() {
    }

    public static PerformanceMetricResource toResourceFromEntity(PerformanceMetric d) {
        return new PerformanceMetricResource(d.getId(), d.getDriverId(), d.getTripId(), d.getVehicleId(), d.getFatigueEvents(), d.getAlertsCount(), d.getAverageHeartRate(), d.getRiskScore(), d.getCalculatedAt());
    }
}
