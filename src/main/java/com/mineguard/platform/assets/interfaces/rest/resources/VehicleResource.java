package com.mineguard.platform.assets.interfaces.rest.resources;

/** Web fleet-inventory vehicle resource (status serialized lowercase). */
public record VehicleResource(Long id, String code, String model, String category, String status,
                              String assignedDriverName, String shiftLabel) {
}
