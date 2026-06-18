package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsIncidentDistribution;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsIncidentDistributionResource;

public final class AnalyticsIncidentDistributionResourceFromEntityAssembler {
    private AnalyticsIncidentDistributionResourceFromEntityAssembler() {
    }

    public static AnalyticsIncidentDistributionResource toResourceFromEntity(AnalyticsIncidentDistribution d) {
        return new AnalyticsIncidentDistributionResource(d.getId(), d.getLabel(), d.getCount(), d.getPercent(), d.getClassName());
    }
}
