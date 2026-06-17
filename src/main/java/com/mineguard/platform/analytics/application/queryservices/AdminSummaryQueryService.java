package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.AdminSummary;

import java.util.List;

public interface AdminSummaryQueryService {
    List<AdminSummary> findAll();
}
