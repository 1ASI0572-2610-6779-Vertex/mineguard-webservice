package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "heart_rate_readings")
@Getter
@Setter
@NoArgsConstructor
public class HeartRateReadingPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "device_id", length = 60)
    private String deviceId;
    @Column(name = "driver_id")
    private Long driverId;
    private double bpm;
    @Column(name = "recorded_at", length = 40)
    private String recordedAt;
}
