package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.FleetSummaryQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetFleetSummaryQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.FleetSummaryResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.FleetSummaryResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/fleet", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet", description = "Aggregated operational state of the company fleet. " +
        "The fleet resource represents the collection of all company vehicles as a single observable unit, " +
        "exposing summary counters and health indicators used by the supervisor dashboard header widget.")
public class FleetSummaryController {

    private final FleetSummaryQueryService fleetSummaryQueryService;

    public FleetSummaryController(FleetSummaryQueryService fleetSummaryQueryService) {
        this.fleetSummaryQueryService = fleetSummaryQueryService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get fleet summary",
            description = "Returns an aggregated snapshot of the current fleet state: total vehicles, " +
                    "active trips, vehicles in transit, vehicles idle, and open alert counts. " +
                    "The response is wrapped in a single-element array to maintain compatibility with " +
                    "the frontend dashboard widget contract (originally designed for a json-server mock). " +
                    "Data is always filtered to the authenticated company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fleet summary returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<FleetSummaryResource>> getSummary() {
        var summary = fleetSummaryQueryService.handle(new GetFleetSummaryQuery())
                .map(FleetSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
        return ResponseEntity.ok(summary);
    }
}
