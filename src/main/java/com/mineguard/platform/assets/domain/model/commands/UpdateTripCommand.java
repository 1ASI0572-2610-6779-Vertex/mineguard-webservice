package com.mineguard.platform.assets.domain.model.commands;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;

/** Command to close (check-out) or cancel an in-progress Driving Session. {@code status} is the only patchable field. */
public record UpdateTripCommand(Long id, TripStatus status) {
}
