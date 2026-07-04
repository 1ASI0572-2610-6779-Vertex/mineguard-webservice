package com.mineguard.platform.assets.interfaces.rest.resources;

/** Response body for a Driving Session (driver check-in) resource. Backed by the Trip aggregate. */
public record DrivingSessionResource(Long id, Long driverId, Long vehicleId, String startTime, String endTime, String status) {
}
