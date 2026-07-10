package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to create an alert from a manual SOS button press on the MineGuard device.
 *
 * <p>Always CRITICAL: a driver pressing the emergency button is the single most explicit
 * distress signal the platform can receive, and it is never inferred from a threshold.</p>
 *
 * @param tripId     active trip the alert is linked to, or null when the device is not assigned to an active trip
 * @param sensorId   database id of the sensor that reported the press
 * @param companyId  tenant id for multi-tenant isolation
 * @param occurredAt ISO-8601 timestamp of the event
 */
public record CreateEmergencySosAlertCommand(
        Long   tripId,
        Long   sensorId,
        Long   companyId,
        String occurredAt
) {}
