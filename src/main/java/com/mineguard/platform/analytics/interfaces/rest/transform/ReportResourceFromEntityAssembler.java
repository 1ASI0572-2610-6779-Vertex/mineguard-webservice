package com.mineguard.platform.analytics.interfaces.rest.transform;

import com.mineguard.platform.analytics.domain.model.aggregates.Report;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportResource;

public final class ReportResourceFromEntityAssembler {
    private ReportResourceFromEntityAssembler() {
    }

    public static ReportResource toResourceFromEntity(Report d) {
        return new ReportResource(d.getId(), d.getIncidentId(), d.getAlertId(), d.getUserId(), d.getMetricId(), d.getReportType(), d.getCreatedAt(), d.getDescription());
    }
}
