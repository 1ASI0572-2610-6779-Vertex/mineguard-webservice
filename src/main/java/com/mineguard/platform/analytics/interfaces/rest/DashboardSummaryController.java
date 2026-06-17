package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardSummaryQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardSummaryResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/dashboardSummary", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class DashboardSummaryController {
    private final DashboardSummaryQueryService queryService;

    public DashboardSummaryController(DashboardSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<DashboardSummaryResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(DashboardSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
