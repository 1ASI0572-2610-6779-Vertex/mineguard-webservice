package com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;

public final class DriverPersistenceAssembler {
    private DriverPersistenceAssembler() {
    }

    public static Driver toDomain(DriverPersistenceEntity e) {
        if (e == null) return null;
        var d = new Driver(e.getFullName(), e.getOperatorId(), e.getLicense(), e.getSpecialty(),
                e.getShiftStatus(), e.getLastAccess(), e.getUserId());
        d.setId(e.getId());
        d.setCompanyId(e.getCompanyId());
        return d;
    }

    public static DriverPersistenceEntity toEntity(Driver d) {
        var e = new DriverPersistenceEntity();
        e.setId(d.getId());
        e.setFullName(d.getFullName());
        e.setOperatorId(d.getOperatorId());
        e.setLicense(d.getLicense());
        e.setSpecialty(d.getSpecialty());
        e.setShiftStatus(d.getShiftStatus());
        e.setLastAccess(d.getLastAccess());
        e.setUserId(d.getUserId());
        e.setCompanyId(d.getCompanyId());
        return e;
    }
}
