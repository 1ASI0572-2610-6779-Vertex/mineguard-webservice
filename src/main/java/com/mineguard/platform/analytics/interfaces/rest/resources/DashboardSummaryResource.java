package com.mineguard.platform.analytics.interfaces.rest.resources;
public record DashboardSummaryResource(Long id, int activeSensors, int totalSensors, int criticalAlerts,
                                       int fatigueEvents, int activeVehicles, int totalDrivers) {}
