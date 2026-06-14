package com.mineguard.platform.assets.domain.model.commands;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;

/** Command to register a vehicle in the inventory. */
public record CreateVehicleCommand(String code, String model, String category, VehicleStatus status,
                                   String assignedDriverName, String shiftLabel) {
}
