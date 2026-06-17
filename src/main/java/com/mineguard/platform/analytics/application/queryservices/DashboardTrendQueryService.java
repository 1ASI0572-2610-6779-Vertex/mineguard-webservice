package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardTrend;

import java.util.List;

public interface DashboardTrendQueryService {
    List<DashboardTrend> findAll();
}
