package com.mineguard.platform.assets.domain.model.aggregates;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Trip aggregate root linking a driver and a vehicle over a time window. */
@Getter
public class Trip extends AbstractDomainAggregateRoot<Trip> {

    @Setter private Long id;
    @Setter private Long driverId;
    @Setter private Long vehicleId;
    @Setter private String startTime;
    @Setter private String endTime;
    @Setter private TripStatus status;

    public Trip() {
    }

    public Trip(Long driverId, Long vehicleId, String startTime, String endTime, TripStatus status) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }
}
