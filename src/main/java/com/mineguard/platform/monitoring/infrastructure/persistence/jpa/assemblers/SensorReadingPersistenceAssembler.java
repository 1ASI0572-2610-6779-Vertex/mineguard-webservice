package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;

public final class SensorReadingPersistenceAssembler {
    private SensorReadingPersistenceAssembler() {
    }

    public static SensorReading toDomain(SensorReadingPersistenceEntity e) {
        if (e == null) return null;
        var r = new SensorReading(e.getSensorId(), e.getReadingType(), e.getValue(), e.getTimestamp());
        r.setId(e.getId());
        return r;
    }

    public static SensorReadingPersistenceEntity toEntity(SensorReading r) {
        var e = new SensorReadingPersistenceEntity();
        e.setId(r.getId());
        e.setSensorId(r.getSensorId());
        e.setReadingType(r.getReadingType());
        e.setValue(r.getValue());
        e.setTimestamp(r.getTimestamp());
        return e;
    }
}
