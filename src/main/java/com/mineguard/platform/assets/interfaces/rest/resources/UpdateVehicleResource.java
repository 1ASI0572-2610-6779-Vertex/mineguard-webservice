package com.mineguard.platform.assets.interfaces.rest.resources;

/**
 * Web update-vehicle request body (PATCH semantics). Every field is optional — {@code null}
 * leaves the current value unchanged (see {@code Vehicle.updateInformation}).
 */
public record UpdateVehicleResource(
        String code,
        String model,
        String category,
        String status,
        String assignedDriverName,
        String shiftLabel) {
}
