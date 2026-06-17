package com.mineguard.platform.analytics.interfaces.rest.resources;

public record ReportDetailResource(Long id, Long incidentId, Long alertId, Long userId, Long metricId,
                                   String reportType, String createdAt, String description,
                                   Object incident, Object alert, Object performanceMetric) {
}
