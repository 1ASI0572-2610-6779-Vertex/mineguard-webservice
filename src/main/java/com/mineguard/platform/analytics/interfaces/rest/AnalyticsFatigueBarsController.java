package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AnalyticsFatigueBarQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AnalyticsFatigueBarResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AnalyticsFatigueBarResourceFromEntityAssembler;
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
@Tag(name = "Analytics", description = "Deep analytical projections over historical driver, vehicle, and alert data. " +
        "Unlike the real-time dashboard sub-resources (under /api/v1/dashboard/), analytics projections " +
        "process longer time windows and may involve heavier computation. " +
        "All projections are tenant-isolated via the JWT companyId.")
public class AnalyticsFatigueBarsController {

    private final AnalyticsFatigueBarQueryService queryService;

    public AnalyticsFatigueBarsController(AnalyticsFatigueBarQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/fatigue-levels")
    @Operation(
            summary = "Get fatigue level distribution",
            description = "Returns per-driver fatigue level bars used to render the fatigue distribution chart " +
                    "in the analytics panel. Each bar represents one driver's aggregated fatigue event count " +
                    "over the selected analysis window, classified into severity bands (low, medium, high, critical). " +
                    "Fatigue events are sourced from Alerts of type `fatigue_risk` and `high_heart_rate` " +
                    "linked to each driver's Trips.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fatigue level data returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AnalyticsFatigueBarResource>> getFatigueLevels() {
        var items = queryService.findAll().stream()
                .map(AnalyticsFatigueBarResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
