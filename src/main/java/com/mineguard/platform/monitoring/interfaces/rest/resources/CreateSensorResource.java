package com.mineguard.platform.monitoring.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Create-device request body. A MineGuard device is a single unified edge unit that already
 * bundles all physical sensors (GPS, heart-rate, ultrasonic proximity, collision), so the admin
 * only links one device to one vehicle — there is no per-sensor registration.
 *
 * <p>The owning {@code companyId} is deliberately NOT a field — it is always resolved from the
 * caller's JWT so a tenant can never provision a device under another company.
 * {@code ignoreUnknown = false} rejects any unrecognized field (including {@code companyId}) with 400.</p>
 *
 * @param vehicleId  the vehicle this device is mounted on (must belong to the caller's company; one device per vehicle)
 * @param deviceId   the identifier the physical device sends as {@code device_id} in its telemetry
 *                   payloads — this is what links incoming telemetry to the vehicle. Keep it simple
 *                   (e.g. "1", "2", "3"); it must match the value configured on the edge unit.
 * @param sensorType optional label; defaults to "MineGuard Device" when omitted
 * @param status     optional lifecycle status; defaults to "active" when omitted
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record CreateSensorResource(
        @NotNull Long vehicleId,
        @NotBlank String deviceId,
        String sensorType,
        String status) {
}