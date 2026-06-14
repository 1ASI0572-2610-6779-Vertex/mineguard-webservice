package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.CardiacReadingPersistenceEntity;

public final class CardiacReadingPersistenceAssembler {
    private CardiacReadingPersistenceAssembler() {
    }

    public static CardiacReading toDomain(CardiacReadingPersistenceEntity e) {
        if (e == null) return null;
        var r = new CardiacReading(e.getDriverName(), e.getVehicleCode(), e.getHeartRate(), e.getStatus());
        r.setId(e.getId());
        return r;
    }

    public static CardiacReadingPersistenceEntity toEntity(CardiacReading r) {
        var e = new CardiacReadingPersistenceEntity();
        e.setId(r.getId());
        e.setDriverName(r.getDriverName());
        e.setVehicleCode(r.getVehicleCode());
        e.setHeartRate(r.getHeartRate());
        e.setStatus(r.getStatus());
        return e;
    }
}
