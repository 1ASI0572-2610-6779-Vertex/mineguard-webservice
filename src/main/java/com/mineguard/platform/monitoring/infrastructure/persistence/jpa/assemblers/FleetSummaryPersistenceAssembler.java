package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.FleetSummaryPersistenceEntity;

public final class FleetSummaryPersistenceAssembler {
    private FleetSummaryPersistenceAssembler() {
    }

    public static FleetSummary toDomain(FleetSummaryPersistenceEntity e) {
        if (e == null) return null;
        var s = new FleetSummary(e.getOperational(), e.getMaintenance(), e.getAlert(), e.getTotal(), e.getOperationalPercent());
        s.setId(e.getId());
        return s;
    }

    public static FleetSummaryPersistenceEntity toEntity(FleetSummary s) {
        var e = new FleetSummaryPersistenceEntity();
        e.setId(s.getId());
        e.setOperational(s.getOperational());
        e.setMaintenance(s.getMaintenance());
        e.setAlert(s.getAlert());
        e.setTotal(s.getTotal());
        e.setOperationalPercent(s.getOperationalPercent());
        return e;
    }
}
