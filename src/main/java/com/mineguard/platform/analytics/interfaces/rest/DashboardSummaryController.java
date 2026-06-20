package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardSummaryQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardSummaryResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Dashboard", description = "Aggregated real-time projections for the supervisor dashboard. " +
        "Dashboard sub-resources are computed views derived from the live state of Drivers, Vehicles, " +
        "Trips, and Alerts. They are read-only and tenant-isolated. " +
        "Each sub-resource is served at its own path under /api/v1/dashboard/ to allow independent " +
        "parallel fetching by the frontend.")
public class DashboardSummaryController {

    private final DashboardSummaryQueryService queryService;

    public DashboardSummaryController(DashboardSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get dashboard summary",
            description = "Returns the main KPI counters for the authenticated company: total active drivers, " +
                    "vehicles in transit, open alerts, active trips, and risk score. " +
                    "This is the first widget loaded by the supervisor dashboard on startup. " +
                    "All counters are computed from the live state of the tenant's assets.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard summary returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DashboardSummaryResource>> getSummary() {
        var items = queryService.findAll().stream()
                .map(DashboardSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
