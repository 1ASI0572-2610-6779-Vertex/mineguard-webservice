package com.mineguard.platform.monitoring.domain.model.commands;

/** Command issued by the mobile app to act on an alert (mark reviewed / call cabin). */
public record MarkAlertActionCommand(Long alertId, String action) {
}
