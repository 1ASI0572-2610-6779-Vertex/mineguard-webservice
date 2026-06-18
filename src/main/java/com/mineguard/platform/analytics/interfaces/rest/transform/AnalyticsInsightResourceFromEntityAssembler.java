package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsInsight;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsInsightResource;

public final class AnalyticsInsightResourceFromEntityAssembler {
    private AnalyticsInsightResourceFromEntityAssembler() {
    }

    public static AnalyticsInsightResource toResourceFromEntity(AnalyticsInsight d) {
        return new AnalyticsInsightResource(d.getId(), d.getTitle(), d.getDescription(), d.getClassName());
    }
}
