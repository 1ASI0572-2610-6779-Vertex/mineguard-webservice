package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.PerformanceMetricQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.PerformanceMetricPersistenceEntity;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerformanceMetricQueryServiceImpl implements PerformanceMetricQueryService {
    private final PerformanceMetricPersistenceRepository repository;

    public PerformanceMetricQueryServiceImpl(PerformanceMetricPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PerformanceMetric> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    private PerformanceMetric toDomain(PerformanceMetricPersistenceEntity e) {
        var d = new PerformanceMetric(e.getDriverId(), e.getTripId(), e.getVehicleId(), e.getFatigueEvents(), e.getAlertsCount(), e.getAverageHeartRate(), e.getRiskScore(), e.getCalculatedAt());
        d.setId(e.getId());
        return d;
    }
}
