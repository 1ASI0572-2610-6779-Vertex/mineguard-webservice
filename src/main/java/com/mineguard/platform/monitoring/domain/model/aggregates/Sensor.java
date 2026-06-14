package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Sensor aggregate root mounted on a vehicle. */
@Getter
public class Sensor extends AbstractDomainAggregateRoot<Sensor> {
    @Setter private Long id;
    @Setter private Long vehicleId;
    @Setter private String sensorType;
    @Setter private String status;

    public Sensor() {
    }

    public Sensor(Long vehicleId, String sensorType, String status) {
        this.vehicleId = vehicleId;
        this.sensorType = sensorType;
        this.status = status;
    }
}
