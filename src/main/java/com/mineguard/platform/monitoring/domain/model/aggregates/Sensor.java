package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Sensor aggregate root mounted on a vehicle. */
@Getter
public class Sensor extends AbstractDomainAggregateRoot<Sensor> {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    /** Terminal state: reserves the deviceId (never recycled) and excludes the device from KPIs. */
    public static final String STATUS_RETIRED = "retired";
    /** Fixed server-side label for a MineGuard unified edge device. */
    public static final String MINEGUARD_DEVICE_TYPE = "MineGuard Device";

    @Setter private Long id;
    @Setter private Long vehicleId;
    @Setter private String sensorType;
    @Setter private String status;
    /** Edge device identifier sent in the X-Device-Id / device_id field of IoT payloads. */
    @Setter private String deviceId;
    /** Owning tenant. Scopes device_id lookups so two companies can never collide on the same device_id. */
    @Setter private Long companyId;

    public Sensor() {
    }

    public Sensor(Long vehicleId, String sensorType, String status) {
        this.vehicleId = vehicleId;
        this.sensorType = sensorType;
        this.status = status;
    }

    public boolean isRetired() {
        return STATUS_RETIRED.equalsIgnoreCase(status);
    }
}
