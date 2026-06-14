package com.mineguard.platform.assets.domain.model.commands;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;

/** Command to update a vehicle inventory record. */
public record UpdateVehicleCommand(Long id, String code, String model, String category, VehicleStatus status,
                                   String assignedDriverName, String shiftLabel) {
}
