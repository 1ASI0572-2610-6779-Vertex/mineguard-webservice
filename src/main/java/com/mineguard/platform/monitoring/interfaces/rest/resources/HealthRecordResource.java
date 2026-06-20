package com.mineguard.platform.monitoring.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Smart-band edge ingestion response body (mirrors the edge service contract). */
public record HealthRecordResource(
        @JsonProperty("id") Long id,
        @JsonProperty("device_id") String deviceId,
        @JsonProperty("bpm") double bpm,
        @JsonProperty("created_at") String createdAt) {
}
