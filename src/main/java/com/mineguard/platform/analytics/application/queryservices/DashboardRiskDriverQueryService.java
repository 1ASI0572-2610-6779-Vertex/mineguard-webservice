package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRiskDriver;

import java.util.List;

public interface DashboardRiskDriverQueryService {
    List<DashboardRiskDriver> findAll();
}
