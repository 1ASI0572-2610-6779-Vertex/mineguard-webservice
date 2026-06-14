package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.SensorPersistenceEntity;

public final class SensorPersistenceAssembler {
    private SensorPersistenceAssembler() {
    }

    public static Sensor toDomain(SensorPersistenceEntity e) {
        if (e == null) return null;
        var s = new Sensor(e.getVehicleId(), e.getSensorType(), e.getStatus());
        s.setId(e.getId());
        return s;
    }

    public static SensorPersistenceEntity toEntity(Sensor s) {
        var e = new SensorPersistenceEntity();
        e.setId(s.getId());
        e.setVehicleId(s.getVehicleId());
        e.setSensorType(s.getSensorType());
        e.setStatus(s.getStatus());
        return e;
    }
}
