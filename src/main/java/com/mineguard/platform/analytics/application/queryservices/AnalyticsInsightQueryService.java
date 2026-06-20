package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsInsight;

import java.util.List;

public interface AnalyticsInsightQueryService {
    List<AnalyticsInsight> findAll();
}
