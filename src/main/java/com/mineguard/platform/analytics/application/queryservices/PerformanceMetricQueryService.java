package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;

import java.util.List;

public interface PerformanceMetricQueryService {
    List<PerformanceMetric> findAll();
}
