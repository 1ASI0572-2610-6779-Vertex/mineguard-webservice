package com.mineguard.platform.iam.domain.model.commands;

/**
 * Command to register (or seed) a smart-band IoT device with its API key.
 */
public record RegisterDeviceCommand(String deviceId, String apiKey, Long driverId) {
}
