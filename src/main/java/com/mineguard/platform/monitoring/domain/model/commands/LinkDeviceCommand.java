package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to link (provision) a MineGuard device onto a vehicle via the nested
 * {@code POST /api/v1/vehicles/{vehicleId}/sensor} endpoint.
 *
 * <p>The request body is empty on purpose: the {@code deviceId} is assigned server-side (the next
 * sequential per-company integer), {@code sensorType} is fixed to {@code "MineGuard Device"} and the
 * initial {@code status} is {@code "active"}. The owning {@code companyId} is resolved from the
 * caller's JWT — never from the body — and the vehicle must belong to it.</p>
 *
 * @param vehicleId the vehicle the device is mounted on (must belong to the caller's company)
 * @param companyId owning tenant, resolved from the authenticated user's JWT
 */
public record LinkDeviceCommand(Long vehicleId, Long companyId) {
}
