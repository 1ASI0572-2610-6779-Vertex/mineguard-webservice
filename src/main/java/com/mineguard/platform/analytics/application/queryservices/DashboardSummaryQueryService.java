package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardSummary;

import java.util.List;

public interface DashboardSummaryQueryService {
    List<DashboardSummary> findAll();
}
