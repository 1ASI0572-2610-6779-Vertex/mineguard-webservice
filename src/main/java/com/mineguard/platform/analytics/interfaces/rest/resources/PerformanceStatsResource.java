package com.mineguard.platform.analytics.interfaces.rest.resources;

/** Mobile operator performance summary: {safetyScore, safetyScoreDelta, fatigueAlerts, drivingHours, drivingHoursLimit}. */
public record PerformanceStatsResource(int safetyScore, int safetyScoreDelta, int fatigueAlerts,
                                       double drivingHours, double drivingHoursLimit) {
}
