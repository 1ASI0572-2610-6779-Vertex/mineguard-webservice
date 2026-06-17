package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.PerformanceMetricQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.interfaces.rest.resources.PerformanceStatsResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Mobile operator performance endpoint ({@code /performance/{workerId}}).
 * Aggregates the operator's performance metrics into the mobile summary contract.
 */
@RestController
@RequestMapping(value = "/performance", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Mobile Performance", description = "Operator performance summary (mobile)")
public class MobilePerformanceController {
    private final PerformanceMetricQueryService performanceMetricQueryService;

    public MobilePerformanceController(PerformanceMetricQueryService performanceMetricQueryService) {
        this.performanceMetricQueryService = performanceMetricQueryService;
    }

    @GetMapping("/{workerId}")
    public ResponseEntity<PerformanceStatsResource> get(@PathVariable String workerId) {
        List<PerformanceMetric> metrics = performanceMetricQueryService.findAll();
        int fatigueAlerts = metrics.stream().mapToInt(PerformanceMetric::getFatigueEvents).sum();
        double avgRisk = metrics.stream().mapToDouble(PerformanceMetric::getRiskScore).average().orElse(20.0);
        int safetyScore = (int) Math.max(0, Math.min(100, Math.round(100 - avgRisk)));
        var stats = new PerformanceStatsResource(safetyScore, 2, fatigueAlerts, 3.5, 8.0);
        return ResponseEntity.ok(stats);
    }
}
