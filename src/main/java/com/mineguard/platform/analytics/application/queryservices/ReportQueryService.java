package com.mineguard.platform.analytics.application.queryservices;

import com.mineguard.platform.analytics.domain.model.aggregates.Report;

import java.util.List;
import java.util.Optional;

public interface ReportQueryService {
    List<Report> findAll();
    Optional<Report> findById(Long id);
}
