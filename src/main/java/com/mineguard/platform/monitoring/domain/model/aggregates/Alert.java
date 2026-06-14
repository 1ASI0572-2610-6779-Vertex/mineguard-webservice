package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.monitoring.application.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.application.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.application.domain.model.valueobjects.AlertType;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Operational alert aggregate root (web {@code operationalAlerts} view). */
@Getter
public class Alert extends AbstractDomainAggregateRoot<Alert> {

    @Setter private Long id;
    @Setter private String code;
    @Setter private AlertType type;
    @Setter private AlertPriority priority;
    @Setter private AlertStatus status;
    @Setter private String occurredAt;
    @Setter private String title;
    @Setter private String description;
    @Setter private String vehicleClassKey;
    @Setter private String vehicleCode;
    @Setter private String driverName;
    @Setter private String resolutionNotes;

    public Alert() {
    }

    public Alert(String code, AlertType type, AlertPriority priority, AlertStatus status, String occurredAt,
                 String title, String description, String vehicleClassKey, String vehicleCode,
                 String driverName, String resolutionNotes) {
        this.code = code;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.occurredAt = occurredAt;
        this.title = title;
        this.description = description;
        this.vehicleClassKey = vehicleClassKey;
        this.vehicleCode = vehicleCode;
        this.driverName = driverName;
        this.resolutionNotes = resolutionNotes;
    }

    /** Updates classification fields (supervisor alert management). */
    public Alert classify(AlertStatus status, String resolutionNotes) {
        if (status != null) this.status = status;
        if (resolutionNotes != null) this.resolutionNotes = resolutionNotes;
        return this;
    }

    public Alert updateAll(AlertType type, AlertPriority priority, AlertStatus status, String title,
                           String description, String vehicleClassKey, String vehicleCode,
                           String driverName, String resolutionNotes) {
        if (type != null) this.type = type;
        if (priority != null) this.priority = priority;
        if (status != null) this.status = status;
        this.title = title;
        this.description = description;
        this.vehicleClassKey = vehicleClassKey;
        this.vehicleCode = vehicleCode;
        this.driverName = driverName;
        this.resolutionNotes = resolutionNotes;
        return this;
    }
}
