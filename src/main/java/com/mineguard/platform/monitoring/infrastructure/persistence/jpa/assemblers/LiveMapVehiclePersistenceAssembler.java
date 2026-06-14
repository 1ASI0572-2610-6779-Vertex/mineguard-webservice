package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.LiveMapVehiclePersistenceEntity;

public final class LiveMapVehiclePersistenceAssembler {
    private LiveMapVehiclePersistenceAssembler() {
    }

    public static LiveMapVehicle toDomain(LiveMapVehiclePersistenceEntity e) {
        if (e == null) return null;
        var v = new LiveMapVehicle(e.getCode(), e.getVehicleType(), e.getLatitude(), e.getLongitude(),
                e.getStatus(), e.getDriverName());
        v.setId(e.getId());
        return v;
    }

    public static LiveMapVehiclePersistenceEntity toEntity(LiveMapVehicle v) {
        var e = new LiveMapVehiclePersistenceEntity();
        e.setId(v.getId());
        e.setCode(v.getCode());
        e.setVehicleType(v.getVehicleType());
        e.setLatitude(v.getLatitude());
        e.setLongitude(v.getLongitude());
        e.setStatus(v.getStatus());
        e.setDriverName(v.getDriverName());
        return e;
    }
}
