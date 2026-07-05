package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to provision a new Sensor and mount it on a Vehicle.
 *
 * @param vehicleId  the vehicle the sensor is mounted on (must belong to the caller's company)
 * @param sensorType descriptive type, e.g. "GPS", "Heart Rate", "Proximity"
 * @param deviceId   physical edge-device identifier — the value the device sends in the
 *                   {@code device_id} field of IoT telemetry payloads. Unique within a company.
 * @param status     lifecycle status, e.g. "active"; defaults to "active" when blank
 * @param companyId  owning tenant, resolved from the authenticated user's JWT (never from the body)
 */
public record CreateSensorCommand(Long vehicleId, String sensorType, String deviceId,
                                  String status, Long companyId) {
}