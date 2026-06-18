package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;

import java.util.List;

public interface AnalyticsHistoryRowQueryService {
    List<AnalyticsHistoryRow> findAll();
}
