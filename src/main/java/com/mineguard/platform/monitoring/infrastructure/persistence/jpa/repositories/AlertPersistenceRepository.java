package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertPersistenceRepository extends JpaRepository<AlertPersistenceEntity, Long> {
    java.util.List<AlertPersistenceEntity> findAllByCompanyId(Long companyId);

    /**
     * Most recent still-open alert of a given kind for a given device. Backs the debounce that stops
     * a sustained condition (e.g. tachycardia) from inserting one alert per telemetry sample.
     * Ordered by {@code occurredAt} descending — it is an ISO-8601 UTC string, so lexicographic
     * ordering matches chronological ordering.
     */
    java.util.Optional<AlertPersistenceEntity> findFirstBySensorIdAndRawTypeAndStatusOrderByOccurredAtDesc(
            Long sensorId, String rawType, AlertStatus status);
}
