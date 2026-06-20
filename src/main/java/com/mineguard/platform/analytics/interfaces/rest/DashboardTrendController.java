package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardTrendQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardTrendResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardTrendResourceFromEntityAssembler;
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
@Tag(name = "Dashboard", description = "Aggregated real-time projections for the supervisor dashboard.")
public class DashboardTrendController {

    private final DashboardTrendQueryService queryService;

    public DashboardTrendController(DashboardTrendQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/trend")
    @Operation(
            summary = "Get alert trend over time",
            description = "Returns the time-series data used to render the alert trend chart on the supervisor dashboard. " +
                    "Each data point represents the count of alerts (by type) aggregated over a time bucket " +
                    "(hour or day depending on the configured window). " +
                    "Used to identify patterns such as fatigue spikes at end-of-shift or proximity incidents " +
                    "in high-traffic zones.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trend data returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DashboardTrendResource>> getTrend() {
        var items = queryService.findAll().stream()
                .map(DashboardTrendResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
