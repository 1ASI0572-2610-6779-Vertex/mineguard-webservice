package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsHistoryRowQueryServiceImpl implements AnalyticsHistoryRowQueryService {
    private final AnalyticsProjectionSupport support;

    public AnalyticsHistoryRowQueryServiceImpl(AnalyticsProjectionSupport support) {
        this.support = support;
    }

    @Override
    public List<AnalyticsHistoryRow> findAll() {
        var alertsById = support.alertsById();
        var tripsById = support.tripsById();
        var vehiclesById = support.vehiclesById();
        return support.incidents().stream().map(i -> {
            var alert = alertsById.get(i.getAlertId());
            var trip = alert == null ? null : tripsById.get(alert.getTripId());
            var vehicle = trip == null ? null : vehiclesById.get(trip.getVehicleId());
            var row = new AnalyticsHistoryRow(support.date(i.getIncidentDate()), support.timeSeconds(i.getIncidentDate()),
                    i.getSeverity(), support.criticalityLabel(i.getSeverity()),
                    alert == null ? "" : support.incidentLabel(alert),
                    vehicle == null ? "" : vehicle.getCode(),
                    support.route(trip));
            row.setId(i.getId());
            return row;
        }).toList();
    }
}
