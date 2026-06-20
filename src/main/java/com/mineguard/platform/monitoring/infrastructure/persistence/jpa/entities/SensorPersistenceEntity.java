package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensors")
@Getter
@Setter
@NoArgsConstructor
public class SensorPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(name = "sensor_type", length = 40)
    private String sensorType;
    @Column(length = 20)
    private String status;
    @Column(name = "device_id", length = 80)
    private String deviceId;
}
