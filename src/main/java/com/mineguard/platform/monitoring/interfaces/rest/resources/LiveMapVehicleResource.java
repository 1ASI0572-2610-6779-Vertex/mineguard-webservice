package com.mineguard.platform.monitoring.interfaces.rest.resources;

public record LiveMapVehicleResource(Long id, String code, String vehicleType, double latitude, double longitude,
                                     String status, String driverName) {
}
