package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.HeartRateReadingPersistenceEntity;

public final class HeartRateReadingPersistenceAssembler {
    private HeartRateReadingPersistenceAssembler() {
    }

    public static HeartRateReading toDomain(HeartRateReadingPersistenceEntity e) {
        if (e == null) return null;
        var r = new HeartRateReading(e.getDeviceId(), e.getDriverId(), e.getBpm(), e.getRecordedAt());
        r.setId(e.getId());
        return r;
    }

    public static HeartRateReadingPersistenceEntity toEntity(HeartRateReading r) {
        var e = new HeartRateReadingPersistenceEntity();
        e.setId(r.getId());
        e.setDeviceId(r.getDeviceId());
        e.setDriverId(r.getDriverId());
        e.setBpm(r.getBpm());
        e.setRecordedAt(r.getCreatedAt());
        return e;
    }
}
