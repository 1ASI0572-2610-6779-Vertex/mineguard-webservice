package com.mineguard.platform.monitoring.domain.model.commands;

import com.mineguard.platform.monitoring.domain.model.valueobjects.CardiacCondition;

/**
 * Command to create an alert derived from a heart-rate sample of the unified telemetry pipeline.
 *
 * <p>The caller has already established that the reading is alert-worthy by running it through
 * {@link CardiacCondition#classify(double)} — this command never re-decides that.</p>
 *
 * @param tripId     active trip the alert is linked to, or null when the device is not assigned to an active trip
 * @param sensorId   database id of the sensor that reported the reading
 * @param companyId  tenant id for multi-tenant isolation
 * @param bpm        the heart rate that breached the threshold, in beats per minute
 * @param condition  which threshold it breached
 * @param occurredAt ISO-8601 timestamp of the event
 */
public record CreateCardiacAlertCommand(
        Long             tripId,
        Long             sensorId,
        Long             companyId,
        double           bpm,
        CardiacCondition condition,
        String           occurredAt
) {}
