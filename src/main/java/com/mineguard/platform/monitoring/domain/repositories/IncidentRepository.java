package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.Incident;

import java.util.List;

public interface IncidentRepository {
    Incident save(Incident incident);
    List<Incident> findAll();
    List<Incident> findAllByCompanyId(Long companyId);
}
