package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Partial-update command for a device via {@code PATCH /api/v1/sensors/{id}}. Covers two operations,
 * either or both of which may be supplied:
 *
 * <ul>
 *   <li><b>Reassignment (move):</b> {@code vehicleId} non-null moves the device to another vehicle
 *       while preserving its {@code deviceId} (the integer that indexes the embedded unit). The
 *       target vehicle must belong to the same tenant and must not already have an active device.</li>
 *   <li><b>Status change:</b> {@code status} non-null sets the lifecycle state
 *       ({@code active} | {@code inactive} | {@code retired}). {@code retired} reserves the id so it
 *       is never recycled, and excludes the device from the active/total sensor KPIs.</li>
 * </ul>
 *
 * <p>{@code null} fields are left unchanged. {@code companyId} is resolved from the JWT and used to
 * enforce tenant ownership of both the device and any move target.</p>
 *
 * @param id        the device (sensor) id to update
 * @param vehicleId optional new vehicle association; {@code null} leaves it unchanged
 * @param status    optional new lifecycle status; {@code null} leaves it unchanged
 * @param companyId owning tenant, resolved from the authenticated user's JWT
 */
public record UpdateSensorCommand(Long id, Long vehicleId, String status, Long companyId) {
}
