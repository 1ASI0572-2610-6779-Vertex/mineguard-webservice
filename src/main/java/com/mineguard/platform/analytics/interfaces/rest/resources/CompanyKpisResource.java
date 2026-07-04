package com.mineguard.platform.analytics.interfaces.rest.resources;

/** Consolidated fleet/catalog/dashboard KPIs for a single company. */
public record CompanyKpisResource(
        Long companyId,
        int driversTotal, int driversInactive,
        int vehiclesTotal, int vehiclesOperational, int vehiclesMaintenance, int vehiclesAlert,
        int vehiclesOperationalPercent,
        int supervisorsTotal, int supervisorsLocked,
        int activeSensors, int totalSensors,
        int criticalAlerts, int fatigueEvents) {
}
