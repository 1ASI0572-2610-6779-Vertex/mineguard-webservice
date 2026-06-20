package com.mineguard.platform.assets.interfaces.rest.resources;

/** Request body for POST /vehicles/{vehicleId}/trips. */
public record CreateTripResource(Long driverId) {
}
