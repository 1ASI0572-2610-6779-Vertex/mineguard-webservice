package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class AlertPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(length = 30)
    private String code;
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AlertType type;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AlertPriority priority;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AlertStatus status;
    @Column(name = "occurred_at", length = 40)
    private String occurredAt;
    @Column(length = 160)
    private String title;
    @Column(length = 500)
    private String description;
    @Column(name = "vehicle_class_key", length = 80)
    private String vehicleClassKey;
    @Column(name = "vehicle_code", length = 40)
    private String vehicleCode;
    @Column(name = "driver_name", length = 120)
    private String driverName;
    @Column(name = "resolution_notes", length = 500)
    private String resolutionNotes;
}
