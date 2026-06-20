package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.PerformanceMetricQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.PerformanceMetricPersistenceEntity;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PerformanceMetricQueryServiceImpl implements PerformanceMetricQueryService {

    private final PerformanceMetricPersistenceRepository repository;
    private final DriverRepository driverRepository;
    private final SecurityContextFacade securityContext;

    public PerformanceMetricQueryServiceImpl(PerformanceMetricPersistenceRepository repository,
                                             DriverRepository driverRepository,
                                             SecurityContextFacade securityContext) {
        this.repository = repository;
        this.driverRepository = driverRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<PerformanceMetric> findAll() {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        // PerformanceMetric no tiene companyId; se filtra por driverIds del tenant
        Set<Long> tenantDriverIds = driverRepository.findAllByCompanyId(companyId).stream()
                .map(Driver::getId)
                .collect(Collectors.toSet());
        return repository.findAll().stream()
                .filter(e -> tenantDriverIds.contains(e.getDriverId()))
                .map(this::toDomain)
                .toList();
    }

    private PerformanceMetric toDomain(PerformanceMetricPersistenceEntity e) {
        var d = new PerformanceMetric(e.getDriverId(), e.getTripId(), e.getVehicleId(), e.getFatigueEvents(),
                e.getAlertsCount(), e.getAverageHeartRate(), e.getRiskScore(), e.getCalculatedAt());
        d.setId(e.getId());
        return d;
    }
}
