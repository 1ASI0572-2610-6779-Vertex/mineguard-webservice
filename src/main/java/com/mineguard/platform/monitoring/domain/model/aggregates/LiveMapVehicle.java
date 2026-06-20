package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Read-model backing the live-map vehicle markers. */
@Getter
public class LiveMapVehicle extends AbstractDomainAggregateRoot<LiveMapVehicle> {
    @Setter private Long id;
    @Setter private Long vehicleId;
    @Setter private String code;
    @Setter private String vehicleType;
    @Setter private double latitude;
    @Setter private double longitude;
    @Setter private String status;
    @Setter private String driverName;

    public LiveMapVehicle() {
    }

    public LiveMapVehicle(String code, String vehicleType, double latitude, double longitude,
                          String status, String driverName) {
        this.code = code;
        this.vehicleType = vehicleType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.driverName = driverName;
    }
}
