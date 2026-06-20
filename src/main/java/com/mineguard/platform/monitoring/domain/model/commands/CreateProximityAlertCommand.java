package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to create a critical proximity/collision alert from edge sensor telemetry.
 *
 * @param tripId      active trip the alert is linked to
 * @param sensorId    database id of the sensor that triggered the event
 * @param companyId   tenant id for multi-tenant isolation
 * @param distanceCm  measured distance in centimetres (null when collision flag triggered it)
 * @param collision   true when the edge device reported a direct collision event
 * @param occurredAt  ISO-8601 timestamp of the event
 */
public record CreateProximityAlertCommand(
        Long    tripId,
        Long    sensorId,
        Long    companyId,
        Integer distanceCm,
        boolean collision,
        String  occurredAt
) {}
