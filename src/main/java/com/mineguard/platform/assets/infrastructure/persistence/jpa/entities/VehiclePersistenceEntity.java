package com.mineguard.platform.assets.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
public class VehiclePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, length = 30)
    private String code;
    @Column(length = 80)
    private String model;
    @Column(length = 80)
    private String category;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private VehicleStatus status;
    @Column(name = "assigned_driver_name", length = 120)
    private String assignedDriverName;
    @Column(name = "shift_label", length = 60)
    private String shiftLabel;
    @Column(name = "driver_id")
    private Long driverId;
    @Column(name = "vehicle_type", length = 80)
    private String vehicleType;
    @Column(name = "company_id")
    private Long companyId;
}
