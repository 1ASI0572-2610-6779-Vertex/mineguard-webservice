package com.mineguard.platform.iam.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** JPA persistence entity for smart-band IoT devices. */
@Entity
@Table(name = "devices")
@Getter
@Setter
@NoArgsConstructor
public class DevicePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "device_id", nullable = false, unique = true, length = 60)
    private String deviceId;

    @Column(name = "api_key", nullable = false, length = 120)
    private String apiKey;

    @Column(name = "driver_id")
    private Long driverId;
}
