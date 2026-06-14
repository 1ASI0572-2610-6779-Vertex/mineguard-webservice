package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cardiac_readings")
@Getter
@Setter
@NoArgsConstructor
public class CardiacReadingPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "driver_name", length = 120)
    private String driverName;
    @Column(name = "vehicle_code", length = 40)
    private String vehicleCode;
    @Column(name = "heart_rate")
    private int heartRate;
    @Column(length = 20)
    private String status;
}
