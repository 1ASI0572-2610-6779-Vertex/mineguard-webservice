package com.mineguard.platform.monitoring.domain.model.commands;

/** Command to ingest a heart-rate reading synchronized from a smart-band edge device. */
public record IngestHeartRateCommand(String deviceId, double bpm, String createdAt, String apiKey) {
}
