package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;

import java.util.List;

public interface SensorRepository {
    Sensor save(Sensor sensor);
    List<Sensor> findAll();
    long count();
}
