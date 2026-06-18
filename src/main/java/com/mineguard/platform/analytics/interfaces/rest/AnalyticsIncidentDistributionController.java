package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsIncidentDistributionQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsIncidentDistributionResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsIncidentDistributionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/analyticsIncidentDistribution", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class AnalyticsIncidentDistributionController {
    private final AnalyticsIncidentDistributionQueryService queryService;

    public AnalyticsIncidentDistributionController(AnalyticsIncidentDistributionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsIncidentDistributionResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(AnalyticsIncidentDistributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
