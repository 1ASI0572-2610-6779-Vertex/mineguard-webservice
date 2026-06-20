package com.mineguard.platform.assets.interfaces.rest.resources;

/** Web update-vehicle request body. */
public record UpdateVehicleResource(String code, String model, String category, String status,
                                    String assignedDriverName, String shiftLabel) {
}
