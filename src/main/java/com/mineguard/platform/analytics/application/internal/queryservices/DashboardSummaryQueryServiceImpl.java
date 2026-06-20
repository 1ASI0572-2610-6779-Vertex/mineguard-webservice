package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.DashboardSummaryQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.DashboardSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardSummaryQueryServiceImpl implements DashboardSummaryQueryService {
    private final AnalyticsProjectionSupport support;

    public DashboardSummaryQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<DashboardSummary> findAll() {
        var sensors = support.sensors();
        var metrics = support.metrics();
        var summary = new DashboardSummary(
                (int) sensors.stream().filter(s -> "active".equalsIgnoreCase(s.getStatus())).count(),
                sensors.size(),
                (int) support.alerts().stream().filter(a -> "high".equalsIgnoreCase(a.getSeverity())).count(),
                metrics.stream().mapToInt(m -> m.getFatigueEvents()).sum(),
                (int) support.activeVehiclesCount(),
                support.drivers().size());
        summary.setId(1L);
        return List.of(summary);
    }
}
