package com.mineguard.platform.monitoring.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Smart-band edge ingestion request body (snake_case wire contract). */
public record HealthDataRecordResource(
        @JsonProperty("device_id") String deviceId,
        @JsonProperty("bpm") double bpm,
        @JsonProperty("created_at") String createdAt) {
}
