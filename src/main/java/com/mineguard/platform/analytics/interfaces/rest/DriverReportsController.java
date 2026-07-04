package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.ReportQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.Report;
import com.mineguard.platform.analytics.infrastructure.persistence.jpa.repositories.PerformanceMetricPersistenceRepository;
import com.mineguard.platform.analytics.interfaces.rest.resources.ReportDetailResource;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.IncidentRepository;
import com.mineguard.platform.shared.infrastructure.export.ExcelExportUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Single Report resource, nested under the Driver it concerns. A Report is produced from an
 * Alert raised during one of the driver's driving sessions — nesting under /drivers/{driverId}
 * makes that ownership chain explicit, same as /trips/{tripId}/cardiac-readings did for trips.
 * Formerly served flat at GET /api/v1/reports/{reportId}.
 */
@RestController
@RequestMapping(value = "/api/v1/drivers/{driverId}/reports", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Driver Reports", description = "Incident/performance reports that belong to a specific driver's " +
        "history, resolved via Report → Alert → DrivingSession → Driver. Supports JSON, PDF, and Excel export.")
public class DriverReportsController {

    private final ReportQueryService queryService;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final TripRepository tripRepository;
    private final PerformanceMetricPersistenceRepository performanceMetricRepository;

    public DriverReportsController(ReportQueryService queryService,
                                    IncidentRepository incidentRepository,
                                    AlertRepository alertRepository,
                                    TripRepository tripRepository,
                                    PerformanceMetricPersistenceRepository performanceMetricRepository) {
        this.queryService = queryService;
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
        this.tripRepository = tripRepository;
        this.performanceMetricRepository = performanceMetricRepository;
    }

    @GetMapping(value = "/{reportId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE})
    @Operation(
            summary = "Get a driver's report — JSON, PDF, or Excel",
            description = "Returns a single report belonging to the specified driver. The representation is " +
                    "selected via the `format` query parameter:\n\n" +
                    "- **Omit `format`** (default): returns the full JSON detail, including the nested Incident, " +
                    "Alert, and PerformanceMetric objects associated with the report.\n\n" +
                    "- **`format=pdf`**: returns the report as a PDF binary file " +
                    "(Content-Type: application/pdf, Content-Disposition: attachment).\n\n" +
                    "- **`format=xls`**: returns the report as an Excel (.xlsx) binary file, one row of fields.\n\n" +
                    "The report must belong to the given `{driverId}` (resolved via Report → Alert → " +
                    "DrivingSession → Driver) AND to the authenticated company — either mismatch returns 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Report returned as JSON (default), PDF, or Excel"),
            @ApiResponse(responseCode = "404", description = "Report not found, not owned by this driver, or not belonging to this tenant"),
            @ApiResponse(responseCode = "400", description = "Unsupported format value (only `pdf`/`xls` are accepted besides the default)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId,
            @Parameter(description = "Unique numeric identifier of the report", required = true)
            @PathVariable("reportId") Long reportId,
            @Parameter(description = "Optional representation format. Pass `pdf` or `xls` to receive the binary file.")
            @RequestParam(required = false) String format) {

        var reportOpt = queryService.findById(reportId);
        if (reportOpt.isEmpty() || !belongsToDriver(reportOpt.get(), driverId)) {
            return ResponseEntity.notFound().build();
        }
        var r = reportOpt.get();

        if ("pdf".equalsIgnoreCase(format)) {
            var content = """
                    MineGuard Incident Report
                    Report ID: %d
                    Driver ID: %d
                    Type: %s
                    Created At: %s

                    %s
                    """.formatted(r.getId(), driverId, r.getReportType(), r.getCreatedAt(), r.getDescription());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=mineguard-report-" + reportId + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(content.getBytes(StandardCharsets.UTF_8));
        }
        if ("xls".equalsIgnoreCase(format)) {
            var headers = List.of("ID", "Driver ID", "Incident ID", "Alert ID", "Metric ID", "Report Type",
                    "Created At", "Description");
            var row = List.of(
                    String.valueOf(r.getId()), String.valueOf(driverId),
                    String.valueOf(r.getIncidentId()), String.valueOf(r.getAlertId()),
                    String.valueOf(r.getMetricId()), nullToEmpty(r.getReportType()),
                    nullToEmpty(r.getCreatedAt()), nullToEmpty(r.getDescription()));
            var bytes = ExcelExportUtil.toXlsx("Report " + r.getId(), headers, List.of(row));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=mineguard-report-" + reportId + ".xlsx")
                    .contentType(MediaType.parseMediaType(ExcelExportUtil.XLSX_CONTENT_TYPE))
                    .body(bytes);
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

    /** Ownership chain: Report -> Alert -> DrivingSession (Trip) -> Driver. */
    private boolean belongsToDriver(Report report, Long driverId) {
        if (report.getAlertId() == null) return false;
        return alertRepository.findById(report.getAlertId())
                .map(alert -> alert.getTripId())
                .flatMap(tripRepository::findById)
                .map(trip -> driverId.equals(trip.getDriverId()))
                .orElse(false);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
