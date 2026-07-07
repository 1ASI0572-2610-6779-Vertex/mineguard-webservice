package com.mineguard.platform.assets.interfaces.rest.resources;

/**
 * Web fleet-inventory vehicle resource (status serialized lowercase).
 *
 * <p>{@code deviceId} is the id of the MineGuard device currently linked to this vehicle (the
 * integer serialized as a string), or {@code null} when the vehicle has no device — this feeds the
 * inventory "without device" filter and the per-row device chip.</p>
 */
public record VehicleResource(Long id, String code, String model, String category, String status,
                              String assignedDriverName, String shiftLabel, String deviceId) {
}
