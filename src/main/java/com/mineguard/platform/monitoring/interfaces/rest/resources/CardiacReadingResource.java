package com.mineguard.platform.monitoring.interfaces.rest.resources;

public record CardiacReadingResource(Long id, String driverName, String vehicleCode, int heartRate, String status) {
}
