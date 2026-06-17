package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsIncidentDistribution;

import java.util.List;

public interface AnalyticsIncidentDistributionQueryService {
    List<AnalyticsIncidentDistribution> findAll();
}
