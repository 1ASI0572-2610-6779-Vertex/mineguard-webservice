package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.LiveMapVehiclePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LiveMapVehiclePersistenceRepository extends JpaRepository<LiveMapVehiclePersistenceEntity, Long> {
    Optional<LiveMapVehiclePersistenceEntity> findByVehicleId(Long vehicleId);
}
