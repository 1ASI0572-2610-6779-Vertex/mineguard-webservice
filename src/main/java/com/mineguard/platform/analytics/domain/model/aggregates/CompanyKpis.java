package com.mineguard.platform.analytics.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

/**
 * Consolidated fleet/catalog/dashboard KPIs for a single company.
 * Replaces the former DashboardSummary, FleetSummary, and CatalogSummary read-models,
 * which duplicated overlapping counters (and, in CatalogSummary's case, were not
 * tenant-scoped at all).
 */
@Getter
public class CompanyKpis extends AbstractDomainAggregateRoot<CompanyKpis> {

    private final Long companyId;
    private final int driversTotal;
    private final int driversInactive;
    private final int vehiclesTotal;
    private final int vehiclesOperational;
    private final int vehiclesMaintenance;
    private final int vehiclesAlert;
    private final int vehiclesOperationalPercent;
    private final int supervisorsTotal;
    private final int supervisorsLocked;
    private final int activeSensors;
    private final int totalSensors;
    private final int criticalAlerts;
    private final int fatigueEvents;

    public CompanyKpis(Long companyId, int driversTotal, int driversInactive, int vehiclesTotal,
                       int vehiclesOperational, int vehiclesMaintenance, int vehiclesAlert,
                       int vehiclesOperationalPercent, int supervisorsTotal, int supervisorsLocked,
                       int activeSensors, int totalSensors, int criticalAlerts, int fatigueEvents) {
        this.companyId = companyId;
        this.driversTotal = driversTotal;
        this.driversInactive = driversInactive;
        this.vehiclesTotal = vehiclesTotal;
        this.vehiclesOperational = vehiclesOperational;
        this.vehiclesMaintenance = vehiclesMaintenance;
        this.vehiclesAlert = vehiclesAlert;
        this.vehiclesOperationalPercent = vehiclesOperationalPercent;
        this.supervisorsTotal = supervisorsTotal;
        this.supervisorsLocked = supervisorsLocked;
        this.activeSensors = activeSensors;
        this.totalSensors = totalSensors;
        this.criticalAlerts = criticalAlerts;
        this.fatigueEvents = fatigueEvents;
    }
}
