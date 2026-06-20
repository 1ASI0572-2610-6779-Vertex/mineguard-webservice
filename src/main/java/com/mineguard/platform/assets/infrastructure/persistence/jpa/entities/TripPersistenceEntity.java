package com.mineguard.platform.assets.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
public class TripPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "driver_id")
    private Long driverId;
    @Column(name = "vehicle_id")
    private Long vehicleId;
    @Column(name = "start_time", length = 40)
    private String startTime;
    @Column(name = "end_time", length = 40)
    private String endTime;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TripStatus status;
    @Column(name = "company_id")
    private Long companyId;
}
