package com.mineguard.platform.monitoring.interfaces.rest.resources;

/**
 * Partial-update body for {@code PATCH /api/v1/sensors/{id}}. Both fields are optional; supply
 * either or both:
 *
 * <ul>
 *   <li>{@code vehicleId} — move the device to another vehicle (preserves the deviceId).</li>
 *   <li>{@code status} — {@code "active"} | {@code "inactive"} | {@code "retired"}.</li>
 * </ul>
 */
public record UpdateSensorResource(Long vehicleId, String status) {
}
