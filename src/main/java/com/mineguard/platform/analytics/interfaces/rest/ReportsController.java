package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.ReportResourceFromEntityAssembler;
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

/**
 * Report collection for the authenticated company. The single-report resource (with PDF/Excel
 * export) is nested under its owning driver — see {@link DriverReportsController} — since a
 * Report is produced in the context of a specific driver's incident history.
 */
@RestController
@RequestMapping(value = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Incident and performance reports for the authenticated company. " +
        "A Report is generated automatically when a critical Alert is resolved or manually by a supervisor. " +
        "For a single report's full detail (JSON, PDF, or Excel), see " +
        "GET /api/v1/drivers/{driverId}/reports/{reportId}.")
public class ReportsController {

    private final ReportQueryService queryService;

    public ReportsController(ReportQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(
            summary = "List reports",
            description = "Returns the list of all reports belonging to the authenticated company. " +
                    "Each item contains the report summary: ID, type, creation date, and a short description. " +
                    "For full detail (including the related Incident, Alert, and PerformanceMetric), " +
                    "fetch the individual report at GET /api/v1/drivers/{driverId}/reports/{reportId}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<ReportResource>> getAll() {
        var items = queryService.findAll().stream()
                .map(ReportResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
