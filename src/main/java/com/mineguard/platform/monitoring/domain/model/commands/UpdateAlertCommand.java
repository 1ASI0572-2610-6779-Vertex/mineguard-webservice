package com.mineguard.platform.monitoring.domain.model.commands;

import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;

/** Command to update/classify an operational alert. */
public record UpdateAlertCommand(Long id, AlertType type, AlertPriority priority, AlertStatus status,
                                 String title, String description, String vehicleClassKey, String vehicleCode,
                                 String driverName, String resolutionNotes) {
}
