package com.mineguard.platform.analytics.interfaces.rest.resources;
import com.fasterxml.jackson.annotation.JsonProperty;
public record ReportResource(
        @JsonProperty("id") Long id,
        @JsonProperty("id_incident") Long incidentId,
        @JsonProperty("id_alert") Long alertId,
        @JsonProperty("id_user") Long userId,
        @JsonProperty("id_metric") Long metricId,
        @JsonProperty("report_type") String reportType,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("description") String description) {}
