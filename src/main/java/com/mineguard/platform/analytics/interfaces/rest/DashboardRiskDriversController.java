package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardRiskDriverQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRiskDriverResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardRiskDriverResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/dashboardRiskDrivers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class DashboardRiskDriversController {
    private final DashboardRiskDriverQueryService queryService;

    public DashboardRiskDriversController(DashboardRiskDriverQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<DashboardRiskDriverResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(DashboardRiskDriverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
