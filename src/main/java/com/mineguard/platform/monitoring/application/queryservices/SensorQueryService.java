package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;

import java.util.List;

public interface SensorQueryService {
    /** Returns all sensors owned by the currently authenticated company (tenant-isolated). */
    List<Sensor> findAllForCurrentCompany();
}