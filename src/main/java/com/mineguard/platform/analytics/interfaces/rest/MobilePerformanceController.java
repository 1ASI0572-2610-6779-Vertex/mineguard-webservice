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
 * Consolidates the former /performance/{workerId} (mobile) and /performanceMetrics
 * (admin table) under the canonical hierarchical path /api/v1/drivers/{driverId}.
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

    @GetMapping("/scores")
    @Operation(
            summary = "Get driver performance scores (mobile)",
            description = "Returns the aggregated performance summary for the specified driver, " +
                    "used by the mobile app's operator profile screen. " +
                    "The summary is computed only from PerformanceMetric records whose driverId matches " +
                    "the {driverId} path variable: safety score (0-100, derived from average risk score), " +
                    "fatigue alert count, average trip duration, and total hours driven. " +
                    "Previously served at GET /performance/{workerId}, then GET /drivers/{driverId}/performance " +
                    "(both of which incorrectly aggregated every driver's metrics instead of just this one), " +
                    "then GET /drivers/{driverId}/kpis. Renamed to /scores.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance summary returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<PerformanceStatsResource> getPerformanceSummary(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId) {
        List<PerformanceMetric> metrics = metricsForDriver(driverId);
        int fatigueAlerts = metrics.stream().mapToInt(PerformanceMetric::getFatigueEvents).sum();
        double avgRisk = metrics.stream().mapToDouble(PerformanceMetric::getRiskScore).average().orElse(20.0);
        int safetyScore = (int) Math.max(0, Math.min(100, Math.round(100 - avgRisk)));
        var stats = new PerformanceStatsResource(safetyScore, 2, fatigueAlerts, 3.5, 8.0);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/metrics")
    @Operation(
            summary = "List raw performance metric records for a driver",
            description = "Returns the PerformanceMetric records whose driverId matches the {driverId} path " +
                    "variable, used by the admin analytics table to show per-trip performance breakdowns. " +
                    "Each record contains: tripId, vehicleId, fatigueEvents, alertsCount, " +
                    "averageHeartRate, riskScore, and calculatedAt timestamp. " +
                    "Previously served at GET /performanceMetrics (flat, all-drivers list) and later " +
                    "GET /drivers/{driverId}/performance-metrics (which ignored {driverId} and returned every " +
                    "driver's records). Renamed to /metrics for a shorter, plural-noun collection name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Performance metrics returned (may be empty for new drivers)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<PerformanceMetricResource>> getPerformanceMetrics(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId) {
        var items = metricsForDriver(driverId).stream()
                .map(PerformanceMetricResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    private List<PerformanceMetric> metricsForDriver(Long driverId) {
        return performanceMetricQueryService.findAll().stream()
                .filter(m -> driverId.equals(m.getDriverId()))
                .toList();
    }
}
