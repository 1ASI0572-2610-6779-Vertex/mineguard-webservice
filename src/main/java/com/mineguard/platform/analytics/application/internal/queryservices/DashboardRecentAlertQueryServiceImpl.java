package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.DashboardRecentAlertQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRecentAlert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardRecentAlertQueryServiceImpl implements DashboardRecentAlertQueryService {
    private final AnalyticsProjectionSupport support;

    public DashboardRecentAlertQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<DashboardRecentAlert> findAll() {
        return support.alerts().stream()
                .filter(a -> a.getStatus() != null && "active".equals(a.getStatus().toSerialized()))
                .sorted(support.newestAlertsFirst())
                .limit(5)
                .map(a -> {
                    var trip = support.tripForAlert(a).orElse(null);
                    var driver = support.driverForAlert(a).orElse(null);
                    var vehicle = support.vehicleForAlert(a).orElse(null);
                    var item = new DashboardRecentAlert(support.alertCode(a), a.getSeverity(), support.category(a),
                            driver == null ? "" : driver.getFullName(),
                            vehicle == null ? "" : vehicle.getCode(),
                            vehicle == null ? "" : vehicle.getVehicleType(),
                            support.route(trip), support.time(a.getOccurredAt()), a.getStatus().toSerialized());
                    item.setId(a.getId());
                    return item;
                }).toList();
    }
}
