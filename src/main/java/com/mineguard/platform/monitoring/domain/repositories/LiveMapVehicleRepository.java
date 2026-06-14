package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;

import java.util.List;

public interface LiveMapVehicleRepository {
    LiveMapVehicle save(LiveMapVehicle vehicle);
    List<LiveMapVehicle> findAll();
}
