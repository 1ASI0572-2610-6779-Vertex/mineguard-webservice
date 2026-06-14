package com.mineguard.platform.assets.domain.repositories;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;

import java.util.List;

public interface TripRepository {
    Trip save(Trip trip);
    List<Trip> findAll();
}
