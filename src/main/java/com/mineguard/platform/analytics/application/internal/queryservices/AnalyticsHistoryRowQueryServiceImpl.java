package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsHistoryRowQueryServiceImpl implements AnalyticsHistoryRowQueryService {
    private final AnalyticsProjectionSupport support;
    private final SecurityContextFacade securityContext;

    public AnalyticsHistoryRowQueryServiceImpl(AnalyticsProjectionSupport support, SecurityContextFacade securityContext) {
        this.support = support;
        this.securityContext = securityContext;
    }

    @Override
    public Page<AnalyticsHistoryRow> findForCompany(Long companyId, Pageable pageable) {
        var callerCompanyId = securityContext.currentCompanyId();
        if (callerCompanyId == null || !callerCompanyId.equals(companyId)) return Page.empty(pageable);

        var alertsById = support.alertsById();
        var tripsById = support.tripsById();
        var vehiclesById = support.vehiclesById();
        List<AnalyticsHistoryRow> all = support.incidents().stream().map(i -> {
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

        int start = (int) pageable.getOffset();
        if (start >= all.size()) {
            return new PageImpl<>(List.of(), pageable, all.size());
        }
        int end = Math.min(start + pageable.getPageSize(), all.size());
        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }
}
