package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardRecentAlertQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRecentAlertResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardRecentAlertResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/dashboardRecentAlerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Analytics and dashboard projections")
public class DashboardRecentAlertsController {
    private final DashboardRecentAlertQueryService queryService;

    public DashboardRecentAlertsController(DashboardRecentAlertQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<List<DashboardRecentAlertResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(DashboardRecentAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
