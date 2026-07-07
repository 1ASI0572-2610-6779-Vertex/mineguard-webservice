package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.CompanyKpisQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.CompanyKpis;
import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Consolidates the former DashboardSummary, FleetSummary, and CatalogSummary queries into a
 * single tenant-scoped read-model, reusing {@link AnalyticsProjectionSupport}'s already-scoped
 * collections instead of re-querying repositories directly (which is how CatalogSummary and
 * FleetSummary previously leaked cross-tenant aggregate counts).
 */
@Service
public class CompanyKpisQueryServiceImpl implements CompanyKpisQueryService {

    private final AnalyticsProjectionSupport support;
    private final SecurityContextFacade securityContext;

    public CompanyKpisQueryServiceImpl(AnalyticsProjectionSupport support, SecurityContextFacade securityContext) {
        this.support = support;
        this.securityContext = securityContext;
    }

    @Override
    public Optional<CompanyKpis> findForCompany(Long companyId) {
        var callerCompanyId = securityContext.currentCompanyId();
        if (callerCompanyId == null || !callerCompanyId.equals(companyId)) return Optional.empty();

        var drivers = support.drivers();
        var vehicles = support.vehicles();
        var sensors = support.sensors();
        var metrics = support.metrics();

        int vehiclesTotal = vehicles.size();
        int vehiclesOperational = (int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.OPERATIONAL).count();
        int vehiclesMaintenance = (int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.MAINTENANCE).count();
        int vehiclesAlert = (int) vehicles.stream().filter(v -> v.getStatus() == VehicleStatus.ALERT
                || v.getStatus() == VehicleStatus.INACTIVE || v.getStatus() == VehicleStatus.RESTRICTED_ROUTE).count();

        // Retired devices reserve their id but no longer count as fleet hardware — exclude them from
        // both the active and total sensor KPIs so the toolbar reflects the real, in-service fleet.
        int activeSensors = (int) sensors.stream()
                .filter(s -> "active".equalsIgnoreCase(s.getStatus())).count();
        int totalSensors = (int) sensors.stream().filter(s -> !s.isRetired()).count();

        var kpis = new CompanyKpis(
                companyId,
                drivers.size(),
                (int) drivers.stream().filter(d -> d.getShiftStatus() == ShiftStatus.INACTIVE).count(),
                vehiclesTotal, vehiclesOperational, vehiclesMaintenance, vehiclesAlert,
                vehiclesTotal == 0 ? 0 : (int) Math.round(vehiclesOperational * 100.0 / vehiclesTotal),
                (int) support.supervisorsCount(),
                (int) support.lockedSupervisorsCount(),
                activeSensors,
                totalSensors,
                (int) support.alerts().stream().filter(a -> "high".equalsIgnoreCase(a.getSeverity())).count(),
                metrics.stream().mapToInt(PerformanceMetric::getFatigueEvents).sum());
        return Optional.of(kpis);
    }
}
