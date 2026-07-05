package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.SensorPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorPersistenceRepository extends JpaRepository<SensorPersistenceEntity, Long> {
    Optional<SensorPersistenceEntity> findByDeviceIdAndCompanyId(String deviceId, Long companyId);
    List<SensorPersistenceEntity> findAllByCompanyId(Long companyId);
    boolean existsByVehicleIdAndCompanyId(Long vehicleId, Long companyId);
}
