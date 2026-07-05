package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository {
    Sensor save(Sensor sensor);
    List<Sensor> findAll();
    List<Sensor> findAllByCompanyId(Long companyId);
    long count();
    /** Tenant-scoped lookup — prevents a device_id collision between two companies from crossing tenants. */
    Optional<Sensor> findByDeviceIdAndCompanyId(String deviceId, Long companyId);
    /** True when the vehicle already has a device linked in this company (enforces one device per vehicle). */
    boolean existsByVehicleIdAndCompanyId(Long vehicleId, Long companyId);
}
