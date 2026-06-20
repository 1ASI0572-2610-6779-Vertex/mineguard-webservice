package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "live_map_vehicles")
@Getter
@Setter
@NoArgsConstructor
public class LiveMapVehiclePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(length = 40)
    private String code;
    @Column(name = "vehicle_type", length = 60)
    private String vehicleType;
    private double latitude;
    private double longitude;
    @Column(length = 20)
    private String status;
    @Column(name = "driver_name", length = 120)
    private String driverName;
}
