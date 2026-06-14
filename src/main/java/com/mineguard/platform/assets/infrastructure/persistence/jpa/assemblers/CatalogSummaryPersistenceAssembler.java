package com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.CatalogSummaryPersistenceEntity;

public final class CatalogSummaryPersistenceAssembler {
    private CatalogSummaryPersistenceAssembler() {
    }

    public static CatalogSummary toDomain(CatalogSummaryPersistenceEntity e) {
        if (e == null) return null;
        var s = new CatalogSummary(e.getDriversTotal(), e.getDriversInactive(), e.getVehiclesTotal(),
                e.getVehiclesMaintenance(), e.getSupervisorsTotal(), e.getSupervisorsLocked());
        s.setId(e.getId());
        return s;
    }

    public static CatalogSummaryPersistenceEntity toEntity(CatalogSummary s) {
        var e = new CatalogSummaryPersistenceEntity();
        e.setId(s.getId());
        e.setDriversTotal(s.getDriversTotal());
        e.setDriversInactive(s.getDriversInactive());
        e.setVehiclesTotal(s.getVehiclesTotal());
        e.setVehiclesMaintenance(s.getVehiclesMaintenance());
        e.setSupervisorsTotal(s.getSupervisorsTotal());
        e.setSupervisorsLocked(s.getSupervisorsLocked());
        return e;
    }
}
