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
 * @param sos        true when the operator pressed the emergency button (absent/null → false)
 * @param lat        GPS latitude (null = not present)
 * @param lng        GPS longitude (null = not present)
 * @param timestamp  ISO-8601 timestamp of the reading (null → server time)
 */
public record TelemetryIngestionRequest(
        @JsonProperty("device_id")   String  deviceId,
        @JsonProperty("bpm")         Double  bpm,
        @JsonProperty("distance_cm") Integer distanceCm,
        @JsonProperty("collision")   Boolean collision,
        @JsonProperty("sos")         Boolean sos,
        @JsonProperty("lat")         Double  lat,
        @JsonProperty("lng")         Double  lng,
        @JsonProperty("timestamp")   String  timestamp
) {
    /**
     * Normalises the optional sensor flags so callers can treat them as primitives.
     *
     * <p>These MUST be boxed types. Jackson 3 enables {@code FAIL_ON_NULL_FOR_PRIMITIVES} by
     * default (Jackson 2 did not), so a primitive component here rejects the whole payload with
     * 400 whenever the field is absent or explicitly null — and a firmware that predates a new
     * field always omits it. Boxing plus this constructor keeps the endpoint backward compatible
     * with every device already in the field.</p>
     */
    public TelemetryIngestionRequest {
        if (bpm == null) bpm = 0.0;
        if (collision == null) collision = false;
        if (sos == null) sos = false;
    }
}
