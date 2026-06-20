package com.mineguard.platform.iot.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wire contract for the unified IoT telemetry ingestion endpoint.
 * Uses strict snake_case as required by the edge computing devices.
 *
 * @param deviceId   unique identifier of the edge sensor/device
 * @param bpm        heart rate in beats per minute (0 = not present)
 * @param distanceCm obstacle proximity reading in centimetres (null = not present)
 * @param collision  true when the edge device detected an impact event
 * @param lat        GPS latitude (null = not present)
 * @param lng        GPS longitude (null = not present)
 * @param timestamp  ISO-8601 timestamp of the reading (null → server time)
 */
public record TelemetryIngestionRequest(
        @JsonProperty("device_id")   String  deviceId,
        @JsonProperty("bpm")         double  bpm,
        @JsonProperty("distance_cm") Integer distanceCm,
        @JsonProperty("collision")   boolean collision,
        @JsonProperty("lat")         Double  lat,
        @JsonProperty("lng")         Double  lng,
        @JsonProperty("timestamp")   String  timestamp
) {}
