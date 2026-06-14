package com.mineguard.platform.assets.interfaces.rest.resources;

/** Web create-vehicle request body. */
public record CreateVehicleResource(String code, String model, String category, String status,
                                    String assignedDriverName, String shiftLabel) {
}
