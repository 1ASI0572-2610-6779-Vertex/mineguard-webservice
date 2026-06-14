package com.mineguard.platform.monitoring.interfaces.rest.resources;

/** Operational alert resource (web). Enums serialized lowercase. */
public record AlertResource(Long id, String code, String type, String priority, String status, String occurredAt,
                            String title, String description, String vehicleClassKey, String vehicleCode,
                            String driverName, String resolutionNotes) {
}
