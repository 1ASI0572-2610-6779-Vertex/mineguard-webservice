package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardRecentAlertQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.DashboardRecentAlertResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.DashboardRecentAlertResourceFromEntityAssembler;
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
public class DashboardRecentAlertsController {

    private final DashboardRecentAlertQueryService queryService;

    public DashboardRecentAlertsController(DashboardRecentAlertQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/recent-alerts")
    @Operation(
            summary = "Get recent alerts for the dashboard feed",
            description = "Returns a short list of the most recently triggered alerts, used to populate the " +
                    "'Recent Alerts' live feed widget on the supervisor dashboard. " +
                    "This is a pre-aggregated projection — it is NOT the full alert collection. " +
                    "For the full alert list with filtering and actions, use GET /api/v1/alerts. " +
                    "Each item includes the alert title, severity, driver name, vehicle code, and elapsed time.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recent alerts returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DashboardRecentAlertResource>> getRecentAlerts() {
        var items = queryService.findAll().stream()
                .map(DashboardRecentAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
