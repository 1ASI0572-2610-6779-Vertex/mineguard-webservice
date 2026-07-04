package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository {
    Sensor save(Sensor sensor);
    List<Sensor> findAll();
    long count();
    /** Tenant-scoped lookup — prevents a device_id collision between two companies from crossing tenants. */
    Optional<Sensor> findByDeviceIdAndCompanyId(String deviceId, Long companyId);
}
