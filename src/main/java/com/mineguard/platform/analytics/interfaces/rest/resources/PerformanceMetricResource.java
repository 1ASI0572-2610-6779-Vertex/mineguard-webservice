package com.mineguard.platform.analytics.interfaces.rest.resources;
import com.fasterxml.jackson.annotation.JsonProperty;
public record PerformanceMetricResource(
        @JsonProperty("id") Long id,
        @JsonProperty("id_driver") Long driverId,
        @JsonProperty("id_trip") Long tripId,
        @JsonProperty("id_vehicle") Long vehicleId,
        @JsonProperty("fatigue_events") int fatigueEvents,
        @JsonProperty("alerts_count") int alertsCount,
        @JsonProperty("average_heart_rate") double averageHeartRate,
        @JsonProperty("risk_score") double riskScore,
        @JsonProperty("calculated_at") String calculatedAt) {}
