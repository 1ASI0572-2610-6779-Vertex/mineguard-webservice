package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardRiskDriverQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRiskDriverResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardRiskDriverResourceFromEntityAssembler;
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
public class DashboardRiskDriversController {

    private final DashboardRiskDriverQueryService queryService;

    public DashboardRiskDriversController(DashboardRiskDriverQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/risk-drivers")
    @Operation(
            summary = "Get highest-risk drivers",
            description = "Returns the ranked list of drivers with the highest current risk score, " +
                    "used to populate the 'At-Risk Drivers' widget on the supervisor dashboard. " +
                    "Risk score is a composite metric derived from: number of fatigue alerts in the current shift, " +
                    "average heart rate deviation from baseline, and number of proximity incidents. " +
                    "Drivers are pre-sorted by risk score descending. " +
                    "For the full per-driver performance breakdown, use GET /api/v1/drivers/{driverId}/performance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Risk driver ranking returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DashboardRiskDriverResource>> getRiskDrivers() {
        var items = queryService.findAll().stream()
                .map(DashboardRiskDriverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
