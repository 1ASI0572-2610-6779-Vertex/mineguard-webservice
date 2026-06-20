package com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.TripPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripPersistenceRepository extends JpaRepository<TripPersistenceEntity, Long> {
    List<TripPersistenceEntity> findAllByCompanyId(Long companyId);
    Optional<TripPersistenceEntity> findFirstByVehicleIdAndStatus(Long vehicleId, TripStatus status);
}
