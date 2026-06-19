package com.mineguard.platform.assets.domain.repositories;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    List<Vehicle> findAll();
    List<Vehicle> findAllByCompanyId(Long companyId);
    long count();
    long countByStatus(com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus status);
}
