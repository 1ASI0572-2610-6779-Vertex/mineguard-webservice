package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.SensorReadingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorReadingPersistenceRepository extends JpaRepository<SensorReadingPersistenceEntity, Long> {
}
