package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sensor_readings")
@Getter
@Setter
@NoArgsConstructor
public class SensorReadingPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "sensor_id")
    private Long sensorId;
    @Column(name = "reading_type", length = 40)
    private String readingType;
    @Column(name = "reading_value")
    private double value;
    @Column(name = "reading_timestamp", length = 40)
    private String timestamp;
}
