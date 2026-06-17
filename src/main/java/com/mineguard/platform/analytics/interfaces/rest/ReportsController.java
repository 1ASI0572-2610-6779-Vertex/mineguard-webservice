package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportDetailResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.ReportResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class ReportsController {
    private final ReportQueryService queryService;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final PerformanceMetricPersistenceRepository performanceMetricRepository;

    public ReportsController(ReportQueryService queryService, IncidentRepository incidentRepository,
                             AlertRepository alertRepository,
                             PerformanceMetricPersistenceRepository performanceMetricRepository) {
        this.queryService = queryService;
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
        this.performanceMetricRepository = performanceMetricRepository;
    }

    @GetMapping
    public ResponseEntity<List<ReportResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(ReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var report = queryService.findById(id);
        if (report.isEmpty()) return ResponseEntity.notFound().build();
        var r = report.get();
        return ResponseEntity.ok(new ReportDetailResource(r.getId(), r.getIncidentId(), r.getAlertId(),
                r.getUserId(), r.getMetricId(), r.getReportType(), r.getCreatedAt(), r.getDescription(),
                incidentRepository.findAll().stream().filter(i -> r.getIncidentId().equals(i.getId())).findFirst().orElse(null),
                alertRepository.findById(r.getAlertId()).orElse(null),
                performanceMetricRepository.findById(r.getMetricId()).orElse(null)));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        var report = queryService.findById(id);
        if (report.isEmpty()) return ResponseEntity.notFound().build();
        var r = report.get();
        var content = """
                MineGuard Incident Report
                Report ID: %d
                Type: %s
                Created At: %s

                %s
                """.formatted(r.getId(), r.getReportType(), r.getCreatedAt(), r.getDescription());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mineguard-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
