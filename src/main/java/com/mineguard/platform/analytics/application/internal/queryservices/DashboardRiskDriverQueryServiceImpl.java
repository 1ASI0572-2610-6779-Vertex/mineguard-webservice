package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.DashboardRiskDriverQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRiskDriver;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardRiskDriverQueryServiceImpl implements DashboardRiskDriverQueryService {
    private final AnalyticsProjectionSupport support;

    public DashboardRiskDriverQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<DashboardRiskDriver> findAll() {
        Map<Long, com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric> byDriver =
                support.metrics().stream().collect(Collectors.toMap(
                        com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric::getDriverId,
                        m -> m,
                        (a, b) -> a.getRiskScore() >= b.getRiskScore() ? a : b));
        return byDriver.values().stream()
                .sorted(Comparator.comparingDouble(com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric::getRiskScore).reversed())

                .map(m -> {
                    var driver = support.driverForMetric(m).orElse(null);
                    var vehicle = support.vehicleForMetric(m).orElse(null);
                    var item = new DashboardRiskDriver(m.getDriverId(),
                            driver == null ? "" : driver.getFullName(),
                            vehicle == null ? "" : vehicle.getVehicleType(),
                            m.getRiskScore());
                    item.setId(m.getId());
                    return item;
                }).toList();
    }
}
