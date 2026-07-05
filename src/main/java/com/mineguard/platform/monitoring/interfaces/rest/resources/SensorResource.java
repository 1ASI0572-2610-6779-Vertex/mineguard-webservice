package com.mineguard.platform.monitoring.interfaces.rest.resources;

/** Read model for a provisioned sensor. */
public record SensorResource(
        Long id,
        Long vehicleId,
        String sensorType,
        String deviceId,
        String status,
        Long companyId) {
}