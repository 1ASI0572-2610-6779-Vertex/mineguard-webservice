package com.mineguard.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response returned to the edge device after a successful telemetry ingestion.
 *
 * @param deviceId    echoes the device that sent the payload
 * @param processed   actions taken by the orchestrator (e.g. "cardiac,location,alert")
 * @param alertRaised true when a proximity/collision alert was triggered
 * @param message     human-readable summary (for edge logs)
 */
public record TelemetryIngestionResponse(
        @JsonProperty("device_id")    String  deviceId,
        @JsonProperty("processed")    String  processed,
        @JsonProperty("alert_raised") boolean alertRaised,
        @JsonProperty("message")      String  message
) {}
