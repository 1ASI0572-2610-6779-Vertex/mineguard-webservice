package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardTrendQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardTrendResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardTrendResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/dashboardTrend", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class DashboardTrendController {
    private final DashboardTrendQueryService queryService;

    public DashboardTrendController(DashboardTrendQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<DashboardTrendResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(DashboardTrendResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
