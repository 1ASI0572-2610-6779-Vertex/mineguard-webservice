package com.mineguard.platform.assets.interfaces.rest.resources;

/** Response body for a Trip (check-in) resource. */
public record TripResource(Long id, Long driverId, Long vehicleId, String startTime, String endTime, String status) {
}
