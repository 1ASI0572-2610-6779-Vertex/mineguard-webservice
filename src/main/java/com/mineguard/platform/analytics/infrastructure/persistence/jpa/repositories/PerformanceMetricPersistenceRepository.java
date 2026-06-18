package com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.PerformanceMetricPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceMetricPersistenceRepository extends JpaRepository<PerformanceMetricPersistenceEntity, Long> {
}
