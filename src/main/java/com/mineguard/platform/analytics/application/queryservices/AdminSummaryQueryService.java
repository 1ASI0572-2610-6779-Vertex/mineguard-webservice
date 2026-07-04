package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AdminSummary;

public interface AdminSummaryQueryService {
    AdminSummary find();
}
