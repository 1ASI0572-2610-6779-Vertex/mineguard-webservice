package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.HeartRateReadingPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRateReadingPersistenceRepository extends JpaRepository<HeartRateReadingPersistenceEntity, Long> {
}
