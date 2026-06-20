package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AdminSummaryQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AdminSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSummaryQueryServiceImpl implements AdminSummaryQueryService {
    private final AnalyticsProjectionSupport support;

    public AdminSummaryQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AdminSummary> findAll() {
        var sensors = support.sensors();
        var summary = new AdminSummary(
                (int) sensors.stream().filter(s -> "active".equalsIgnoreCase(s.getStatus())).count(),
                sensors.size(),
                (int) support.lockedSupervisorsCount(),
                support.vehicles().size() + support.drivers().size());
        summary.setId(1L);
        return List.of(summary);
    }
}
