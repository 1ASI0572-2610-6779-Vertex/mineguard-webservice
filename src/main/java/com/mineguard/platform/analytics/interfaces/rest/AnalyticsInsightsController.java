package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsInsightQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsInsightResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsInsightResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/analyticsInsights", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class AnalyticsInsightsController {
    private final AnalyticsInsightQueryService queryService;

    public AnalyticsInsightsController(AnalyticsInsightQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsInsightResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(AnalyticsInsightResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
