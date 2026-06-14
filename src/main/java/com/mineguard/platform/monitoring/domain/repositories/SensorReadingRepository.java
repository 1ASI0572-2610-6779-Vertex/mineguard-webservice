package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;

import java.util.List;

public interface SensorReadingRepository {
    SensorReading save(SensorReading reading);
    List<SensorReading> findAll();
}
