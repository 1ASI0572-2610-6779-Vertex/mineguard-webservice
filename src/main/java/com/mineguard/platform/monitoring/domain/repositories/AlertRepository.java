package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    Alert save(Alert alert);
    Optional<Alert> findById(Long id);
    List<Alert> findAll();
    List<Alert> findAllByCompanyId(Long companyId);

    /** Most recent alert of {@code rawType} for {@code sensorId} that is still ACTIVE, if any. */
    Optional<Alert> findLatestActiveBySensorIdAndRawType(Long sensorId, String rawType);
}
