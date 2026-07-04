package com.mineguard.platform.assets.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

/** Request body for POST /vehicles/{vehicleId}/driving-sessions. */
public record CreateDrivingSessionResource(@NotNull Long driverId) {
}
