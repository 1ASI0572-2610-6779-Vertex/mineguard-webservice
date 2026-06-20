package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsIncidentDistributionQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsIncidentDistributionResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsIncidentDistributionResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Analytics", description = "Deep analytical projections over historical driver, vehicle, and alert data.")
public class AnalyticsIncidentDistributionController {

    private final AnalyticsIncidentDistributionQueryService queryService;

    public AnalyticsIncidentDistributionController(AnalyticsIncidentDistributionQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/incident-distribution")
    @Operation(
            summary = "Get incident type distribution",
            description = "Returns the breakdown of incidents by type (proximity_collision, restricted_zone_entry, " +
                    "high_heart_rate, fatigue_risk, connection_lost), used to render the pie or donut chart " +
                    "in the analytics panel. " +
                    "Each entry contains the incident type key, the human-readable label, and the count and " +
                    "percentage relative to the total. " +
                    "Data is sourced from the Incident aggregate, filtered to the authenticated company's trips.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Incident distribution returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsIncidentDistributionResource>> getIncidentDistribution() {
        var items = queryService.findAll().stream()
                .map(AnalyticsIncidentDistributionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
