package com.mineguard.platform.monitoring.interfaces.rest.resources;

/** Update-alert request body (web alert management / classification). */
public record UpdateAlertResource(String code, String type, String priority, String status, String occurredAt,
                                  String title, String description, String vehicleClassKey, String vehicleCode,
                                  String driverName, String resolutionNotes) {
}
