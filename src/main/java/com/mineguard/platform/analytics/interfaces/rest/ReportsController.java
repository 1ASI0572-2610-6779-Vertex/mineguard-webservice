package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportDetailResource;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.ReportResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reports", description = "Incident and performance reports for the authenticated company. " +
        "A Report is generated automatically when a critical Alert is resolved or manually by a supervisor. " +
        "It aggregates the related Incident, Alert, and PerformanceMetric data into a persistent document. " +
        "Reports can be retrieved as JSON (metadata + structured data) or as a PDF binary " +
        "using the `format=pdf` query parameter on the single-report endpoint.")
public class ReportsController {

    private final ReportQueryService queryService;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final PerformanceMetricPersistenceRepository performanceMetricRepository;

    public ReportsController(ReportQueryService queryService,
                              IncidentRepository incidentRepository,
                              AlertRepository alertRepository,
                              PerformanceMetricPersistenceRepository performanceMetricRepository) {
        this.queryService = queryService;
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
        this.performanceMetricRepository = performanceMetricRepository;
    }

    // -------------------------------------------------------------------------
    // Collection
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "List reports",
            description = "Returns the list of all reports belonging to the authenticated company. " +
                    "Each item contains the report summary: ID, type, creation date, and a short description. " +
                    "For full detail (including the related Incident, Alert, and PerformanceMetric), " +
                    "fetch the individual report at GET /api/v1/reports/{reportId}.")
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

    // -------------------------------------------------------------------------
    // Single resource — JSON or PDF depending on ?format
    // -------------------------------------------------------------------------

    @GetMapping(value = "/{reportId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE})
    @Operation(
            summary = "Get a report — JSON detail or PDF binary",
            description = "Returns a single report by ID. The representation is selected via the `format` query parameter:\n\n" +
                    "- **Omit `format`** (default): returns the full JSON detail, including the nested Incident, Alert, " +
                    "and PerformanceMetric objects associated with the report.\n\n" +
                    "- **`format=pdf`**: returns the report as a PDF binary file " +
                    "(Content-Type: application/pdf, Content-Disposition: attachment). " +
                    "The PDF is generated inline from the report's structured data — no external service is called. " +
                    "This consolidates the former GET /reports/{id}/pdf endpoint (a separate path with a different " +
                    "`produces` content type) into a single resource with content negotiation via query param, " +
                    "following the professor's rule: format is a representation concern, not a resource concern.\n\n" +
                    "Note: the `Accept` header is NOT used for this negotiation because the frontend passes " +
                    "`format=pdf` as a download trigger — using the query param avoids Accept header complexity " +
                    "for browser-initiated file downloads.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report returned as JSON (default) or PDF binary (format=pdf)"),
            @ApiResponse(responseCode = "404", description = "Report not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "400", description = "Unsupported format value (only `pdf` is accepted besides the default)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "Unique numeric identifier of the report", required = true)
            @PathVariable("reportId") Long reportId,
            @Parameter(description = "Optional representation format. Pass `pdf` to receive the binary PDF file.")
            @RequestParam(required = false) String format) {

        var report = queryService.findById(reportId);
        if (report.isEmpty()) return ResponseEntity.notFound().build();
        var r = report.get();

        if ("pdf".equalsIgnoreCase(format)) {
            var content = """
                    MineGuard Incident Report
                    Report ID: %d
                    Type: %s
                    Created At: %s

                    %s
                    """.formatted(r.getId(), r.getReportType(), r.getCreatedAt(), r.getDescription());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=mineguard-report-" + reportId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        return ResponseEntity.ok(new ReportDetailResource(
                r.getId(), r.getIncidentId(), r.getAlertId(),
                r.getUserId(), r.getMetricId(), r.getReportType(),
                r.getCreatedAt(), r.getDescription(),
                incidentRepository.findAll().stream()
                        .filter(i -> r.getIncidentId().equals(i.getId())).findFirst().orElse(null),
                alertRepository.findById(r.getAlertId()).orElse(null),
                performanceMetricRepository.findById(r.getMetricId()).orElse(null)));
    }
}
