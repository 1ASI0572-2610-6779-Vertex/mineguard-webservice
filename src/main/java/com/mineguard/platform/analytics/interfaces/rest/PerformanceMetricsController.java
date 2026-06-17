package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.PerformanceMetricQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.PerformanceMetricResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.PerformanceMetricResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/performanceMetrics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class PerformanceMetricsController {
    private final PerformanceMetricQueryService queryService;

    public PerformanceMetricsController(PerformanceMetricQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<PerformanceMetricResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(PerformanceMetricResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
