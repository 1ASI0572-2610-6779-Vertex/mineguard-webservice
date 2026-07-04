package com.mineguard.platform.monitoring.interfaces.rest.resources;

import jakarta.validation.constraints.Size;

/**
 * Update-alert request body (web alert management / classification). Partial update (PATCH) —
 * every field is optional and {@code null} leaves the current value unchanged.
 */
public record UpdateAlertResource(
        String code, String type, String priority, String status, String occurredAt,
        @Size(max = 160) String title,
        @Size(max = 2000) String description,
        String vehicleClassKey, String vehicleCode, String driverName,
        @Size(max = 2000) String resolutionNotes) {
}
