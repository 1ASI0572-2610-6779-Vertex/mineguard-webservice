package com.mineguard.platform.assets.domain.model.commands;

/**
 * Command to archive (decommission) a vehicle — a soft delete that preserves the row and all its
 * history (incidents, telemetry, sessions). Rejected while the vehicle still has an active device:
 * the supervisor must move or retire the device first so the hardware is never left orphaned.
 *
 * @param id the vehicle to archive (must belong to the caller's company)
 */
public record ArchiveVehicleCommand(Long id) {
}
