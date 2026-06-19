package com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;

public final class VehiclePersistenceAssembler {
    private VehiclePersistenceAssembler() {
    }

    public static Vehicle toDomain(VehiclePersistenceEntity e) {
        if (e == null) return null;
        var v = new Vehicle(e.getCode(), e.getModel(), e.getCategory(), e.getStatus(),
                e.getAssignedDriverName(), e.getShiftLabel());
        v.setId(e.getId());
        v.setDriverId(e.getDriverId());
        v.setVehicleType(e.getVehicleType());
        v.setCompanyId(e.getCompanyId());
        return v;
    }

    public static VehiclePersistenceEntity toEntity(Vehicle v) {
        var e = new VehiclePersistenceEntity();
        e.setId(v.getId());
        e.setCode(v.getCode());
        e.setModel(v.getModel());
        e.setCategory(v.getCategory());
        e.setStatus(v.getStatus());
        e.setAssignedDriverName(v.getAssignedDriverName());
        e.setShiftLabel(v.getShiftLabel());
        e.setDriverId(v.getDriverId());
        e.setVehicleType(v.getVehicleType());
        e.setCompanyId(v.getCompanyId());
        return e;
    }
}
