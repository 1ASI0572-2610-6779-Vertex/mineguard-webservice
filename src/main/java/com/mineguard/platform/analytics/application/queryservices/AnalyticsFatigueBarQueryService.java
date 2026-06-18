package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsFatigueBar;

import java.util.List;

public interface AnalyticsFatigueBarQueryService {
    List<AnalyticsFatigueBar> findAll();
}
