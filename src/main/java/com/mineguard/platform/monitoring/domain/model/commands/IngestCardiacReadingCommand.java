package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to persist a cardiac (heart-rate) reading from the unified IoT telemetry pipeline.
 * Authentication is pre-validated by the EdgeApiKeyFilter — no apiKey field required here.
 *
 * @param sensorId    database id of the sensor that reported the reading
 * @param vehicleId   vehicle the sensor is mounted on
 * @param bpm         heart rate in beats per minute
 * @param occurredAt  ISO-8601 timestamp of the reading
 */
public record IngestCardiacReadingCommand(Long sensorId, Long vehicleId, double bpm, String occurredAt) {}
