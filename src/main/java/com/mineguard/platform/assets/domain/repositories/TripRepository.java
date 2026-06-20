package com.mineguard.platform.assets.domain.repositories;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;

import java.util.List;
import java.util.Optional;

public interface TripRepository {
    Trip save(Trip trip);
    List<Trip> findAll();
    List<Trip> findAllByCompanyId(Long companyId);
    Optional<Trip> findFirstByVehicleIdAndStatus(Long vehicleId, TripStatus status);
}
