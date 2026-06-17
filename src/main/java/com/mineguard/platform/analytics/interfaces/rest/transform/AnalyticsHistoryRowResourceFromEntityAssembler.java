package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsHistoryRowResource;

public final class AnalyticsHistoryRowResourceFromEntityAssembler {
    private AnalyticsHistoryRowResourceFromEntityAssembler() {
    }

    public static AnalyticsHistoryRowResource toResourceFromEntity(AnalyticsHistoryRow d) {
        return new AnalyticsHistoryRowResource(d.getId(), d.getDate(), d.getTime(), d.getCriticality(), d.getCriticalityLabel(), d.getIncidentType(), d.getInvolved(), d.getLocation());
    }
}
