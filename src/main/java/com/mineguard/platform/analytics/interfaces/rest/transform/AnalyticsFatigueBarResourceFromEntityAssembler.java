package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsFatigueBar;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsFatigueBarResource;

public final class AnalyticsFatigueBarResourceFromEntityAssembler {
    private AnalyticsFatigueBarResourceFromEntityAssembler() {
    }

    public static AnalyticsFatigueBarResource toResourceFromEntity(AnalyticsFatigueBar d) {
        return new AnalyticsFatigueBarResource(d.getId(), d.getDriverId(), d.getDriverName(), d.getFatigueEvents(), d.getWidth());
    }
}
