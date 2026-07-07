package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorQueryService {
    /** Returns all sensors owned by the currently authenticated company (tenant-isolated). */
    List<Sensor> findAllForCurrentCompany();

    /**
     * Returns the non-retired device currently mounted on the given vehicle within the caller's
     * company, or empty if the vehicle has no device (or does not belong to the tenant).
     */
    Optional<Sensor> findActiveByVehicleForCurrentCompany(Long vehicleId);
}