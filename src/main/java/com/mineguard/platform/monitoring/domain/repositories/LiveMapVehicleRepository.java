package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;

import java.util.List;
import java.util.Optional;

public interface LiveMapVehicleRepository {
    LiveMapVehicle save(LiveMapVehicle vehicle);
    List<LiveMapVehicle> findAll();
    Optional<LiveMapVehicle> findByVehicleId(Long vehicleId);
}
