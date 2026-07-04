package com.mineguard.platform.analytics.application.internal.queryservices;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.Report;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities.ReportPersistenceEntity;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.ReportPersistenceRepository;
import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReportQueryServiceImpl implements ReportQueryService {
    private final ReportPersistenceRepository repository;
    private final UserRepository userRepository;
    private final SecurityContextFacade securityContext;

    public ReportQueryServiceImpl(ReportPersistenceRepository repository, UserRepository userRepository,
                                  SecurityContextFacade securityContext) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.securityContext = securityContext;
    }

    @Override
    public List<Report> findAll() {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        Set<Long> tenantUserIds = userRepository.findAll().stream()
                .filter(u -> companyId.equals(u.getCompanyId()))
                .map(User::getId)
                .collect(Collectors.toSet());
        return repository.findAll().stream()
                .filter(e -> tenantUserIds.contains(e.getUserId()))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Report> findById(Long id) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return Optional.empty();
        return repository.findById(id)
                .filter(e -> e.getUserId() != null && userRepository.findById(e.getUserId())
                        .map(u -> companyId.equals(u.getCompanyId()))
                        .orElse(false))
                .map(this::toDomain);
    }

    private Report toDomain(ReportPersistenceEntity e) {
        var d = new Report(e.getIncidentId(), e.getAlertId(), e.getUserId(), e.getMetricId(), e.getReportType(), e.getGeneratedAt(), e.getDescription());
        d.setId(e.getId());
        return d;
    }
}
