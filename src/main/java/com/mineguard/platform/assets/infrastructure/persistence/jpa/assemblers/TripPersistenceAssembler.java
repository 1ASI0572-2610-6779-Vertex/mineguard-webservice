package com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.TripPersistenceEntity;

public final class TripPersistenceAssembler {
    private TripPersistenceAssembler() {
    }

    public static Trip toDomain(TripPersistenceEntity e) {
        if (e == null) return null;
        var t = new Trip(e.getDriverId(), e.getVehicleId(), e.getStartTime(), e.getEndTime(), e.getStatus());
        t.setId(e.getId());
        t.setCompanyId(e.getCompanyId());
        return t;
    }

    public static TripPersistenceEntity toEntity(Trip t) {
        var e = new TripPersistenceEntity();
        e.setId(t.getId());
        e.setDriverId(t.getDriverId());
        e.setVehicleId(t.getVehicleId());
        e.setStartTime(t.getStartTime());
        e.setEndTime(t.getEndTime());
        e.setStatus(t.getStatus());
        e.setCompanyId(t.getCompanyId());
        return e;
    }
}
