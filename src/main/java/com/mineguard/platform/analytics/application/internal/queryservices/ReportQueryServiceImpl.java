package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.Report;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.ReportPersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportQueryServiceImpl implements ReportQueryService {
    private final ReportPersistenceRepository repository;

    public ReportQueryServiceImpl(ReportPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Report> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Report> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    private Report toDomain(ReportPersistenceEntity e) {
        var d = new Report(e.getIncidentId(), e.getAlertId(), e.getUserId(), e.getMetricId(), e.getReportType(), e.getGeneratedAt(), e.getDescription());
        d.setId(e.getId());
        return d;
    }
}
