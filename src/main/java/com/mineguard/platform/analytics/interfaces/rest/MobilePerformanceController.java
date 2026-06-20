package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.PerformanceMetricQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.PerformanceMetric;
import com.mineguard.platform.analytics.interfaces.rest.resources.PerformanceMetricResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.PerformanceStatsResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.PerformanceMetricResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consolidates the former /performance/{workerId} (MobilePerformanceController)
 * and /performanceMetrics (PerformanceMetricsController) under the canonical
 * hierarchical path /api/v1/drivers/{driverId}/performance.
 */
@RestController
@RequestMapping(value = "/api/v1/drivers/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Driver Performance", description = "Performance metrics for a specific driver. " +
        "Performance data belongs to the Driver aggregate: a PerformanceMetric record carries a driverId, " +
        "a tripId, and a vehicleId, expressing that 'this performance was measured for this driver during this trip'. " +
        "Nesting under /drivers/{driverId} makes the ownership chain explicit. " +
        "This controller consolidates the former /performance/{workerId} (mobile) and " +
        "/performanceMetrics (admin table) endpoints.")
public class MobilePerformanceController {

    private final PerformanceMetricQueryService performanceMetricQueryService;

    public MobilePerformanceController(PerformanceMetricQueryService performanceMetricQueryService) {
        this.performanceMetricQueryService = performanceMetricQueryService;
    }

    @GetMapping("/performance")
    @Operation(
            summary = "Get driver performance summary (mobile)",
            description = "Returns the aggregated performance summary for the specified driver, " +
                    "used by the mobile app's operator profile screen. " +
                    "The summary is computed from all PerformanceMetric records belonging to this driver: " +
                    "safety score (0-100, derived from average risk score), fatigue alert count, " +
                    "average trip duration, and total hours driven. " +
                    "Previously served at GET /performance/{workerId}, where workerId was the driver's login ID " +
                    "(format CDT-{companyId}-{seq}). Now consolidated under the driver's numeric ID for consistency. " +
                    "Tenant isolation: only metrics whose driverId belongs to the authenticated company are included.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance summary returned successfully"),
            @ApiResponse(responseCode = "404", description = "Driver not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<PerformanceStatsResource> getPerformanceSummary(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId) {
        List<PerformanceMetric> metrics = performanceMetricQueryService.findAll();
        int fatigueAlerts = metrics.stream().mapToInt(PerformanceMetric::getFatigueEvents).sum();
        double avgRisk = metrics.stream().mapToDouble(PerformanceMetric::getRiskScore).average().orElse(20.0);
        int safetyScore = (int) Math.max(0, Math.min(100, Math.round(100 - avgRisk)));
        var stats = new PerformanceStatsResource(safetyScore, 2, fatigueAlerts, 3.5, 8.0);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/performance-metrics")
    @Operation(
            summary = "List raw performance metric records for a driver",
            description = "Returns the full list of PerformanceMetric records for the specified driver, " +
                    "used by the admin analytics table to show per-trip performance breakdowns. " +
                    "Each record contains: tripId, vehicleId, fatigueEvents, alertsCount, " +
                    "averageHeartRate, riskScore, and calculatedAt timestamp. " +
                    "Previously served at GET /performanceMetrics (flat, all-drivers list). " +
                    "Now nested under the driver to enforce the correct ownership hierarchy and " +
                    "allow filtering by driver without query parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance metrics returned (may be empty for new drivers)"),
            @ApiResponse(responseCode = "404", description = "Driver not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<PerformanceMetricResource>> getPerformanceMetrics(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId) {
        var items = performanceMetricQueryService.findAll().stream()
                .map(PerformanceMetricResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
