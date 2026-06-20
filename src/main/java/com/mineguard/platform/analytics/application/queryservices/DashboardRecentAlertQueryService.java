package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRecentAlert;

import java.util.List;

public interface DashboardRecentAlertQueryService {
    List<DashboardRecentAlert> findAll();
}
