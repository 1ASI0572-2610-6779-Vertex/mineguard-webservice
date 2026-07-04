package com.mineguard.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response returned to the edge device after a successful telemetry ingestion.
 *
 * @param deviceId    echoes the device that sent the payload
 * @param processed   actions taken by the orchestrator, as a JSON array (e.g. {@code ["cardiac", "location", "alert"]})
 *                    — an array, not a comma-joined string, so C/C++ firmware can parse it without ad-hoc splitting
 * @param alertRaised true when a proximity/collision alert was triggered
 * @param message     human-readable summary (for edge logs)
 */
public record TelemetryIngestionResponse(
        @JsonProperty("device_id")    String       deviceId,
        @JsonProperty("processed")    List<String> processed,
        @JsonProperty("alert_raised") boolean      alertRaised,
        @JsonProperty("message")      String       message
) {}
