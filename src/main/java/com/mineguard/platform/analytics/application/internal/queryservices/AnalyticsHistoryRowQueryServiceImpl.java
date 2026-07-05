package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsHistoryRowQueryService;
import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.AnalyticsHistoryRow;
import com.mineguard.platform.analytics.domain.model.aggregates.Report;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsHistoryRowQueryServiceImpl implements AnalyticsHistoryRowQueryService {
    private final AnalyticsProjectionSupport support;
    private final SecurityContextFacade securityContext;
    private final ReportQueryService reportQueryService;

    public AnalyticsHistoryRowQueryServiceImpl(AnalyticsProjectionSupport support, SecurityContextFacade securityContext,
                                               ReportQueryService reportQueryService) {
        this.support = support;
        this.securityContext = securityContext;
        this.reportQueryService = reportQueryService;
    }

    @Override
    public Page<AnalyticsHistoryRow> findForCompany(Long companyId, Pageable pageable) {
        var callerCompanyId = securityContext.currentCompanyId();
        if (callerCompanyId == null || !callerCompanyId.equals(companyId)) return Page.empty(pageable);

        var alertsById = support.alertsById();
        var tripsById = support.tripsById();
        var vehiclesById = support.vehiclesById();
        // Reports linked by incident, so each history row can expose the reportId needed by
        // GET /drivers/{driverId}/reports/{reportId}. Tenant-scoped inside ReportQueryService.
        Map<Long, Long> reportIdByIncidentId = reportQueryService.findAll().stream()
                .filter(r -> r.getIncidentId() != null)
                .collect(Collectors.toMap(Report::getIncidentId, Report::getId, (a, b) -> a));
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
            row.setDriverId(trip == null ? null : trip.getDriverId());
            row.setReportId(reportIdByIncidentId.get(i.getId()));
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
