package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.queryservices.AlertQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.queries.GetAlertByIdQuery;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.repositories.AuditLogEntryRepository;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertHistoryResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.AlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.MobileAlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.UpdateAlertResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.interfaces.rest.transform.MobileAlertResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.interfaces.rest.transform.UpdateAlertCommandFromResourceAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * Canonical alerts controller. Consolidates the former {@code /alerts} (mobile)
 * and {@code /operationalAlerts} (web) into a single resource collection.
 */
@RestController
@RequestMapping(value = "/api/v1/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Safety alert collection for the authenticated company. " +
        "Alerts are generated automatically by the monitoring engine when a sensor reading — " +
        "heart-rate from a smart-band, proximity from a collision radar, or zone-entry from GPS — " +
        "crosses a configured threshold during an active Trip. " +
        "Alerts are the canonical resource; the former /operationalAlerts and /alerts (mobile) " +
        "endpoints are now unified here, differentiated only by the optional `view` query parameter.")
public class MobileAlertsController {

    private final AlertQueryService alertQueryService;
    private final AlertCommandService alertCommandService;
    private final AuditLogEntryRepository auditLogEntryRepository;

    public MobileAlertsController(AlertQueryService alertQueryService,
                                   AlertCommandService alertCommandService,
                                   AuditLogEntryRepository auditLogEntryRepository) {
        this.alertQueryService = alertQueryService;
        this.alertCommandService = alertCommandService;
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    // -------------------------------------------------------------------------
    // Collection
    // -------------------------------------------------------------------------

    @GetMapping
    @Operation(
            summary = "List alerts",
            description = "Returns all alerts belonging to the authenticated company (tenant-isolated via JWT). " +
                    "By default, only non-resolved alerts are returned — suitable for the mobile supervisor app. " +
                    "Pass `view=operational` to receive the full enriched payload used by the web panel " +
                    "(formerly served by the /operationalAlerts endpoint, now consolidated here). " +
                    "Alerts are enriched at query time with driver name, vehicle code, and incident description " +
                    "by cross-referencing the Trip that originated each alert. " +
                    "Pass `sort=-occurredAt` to rank alerts newest-first and `limit=N` to cap the result size " +
                    "— e.g. `GET /api/v1/alerts?sort=-occurredAt&limit=10` for the dashboard's 'Recent Alerts' " +
                    "feed (replaces the former GET /dashboard/recent-alerts widget endpoint).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<?>> getAll(
            @Parameter(description = "Optional view variant. `operational` returns the full web payload " +
                    "(all statuses, enriched fields); omitting it returns only non-resolved alerts (mobile payload).")
            @RequestParam(required = false) String view,
            @Parameter(description = "Optional sort key. Accepted value: `-occurredAt` (newest first)")
            @RequestParam(required = false) String sort,
            @Parameter(description = "Optional maximum number of results to return")
            @RequestParam(required = false) Integer limit) {
        var all = alertQueryService.handle(new GetAllAlertsQuery());
        if ("-occurredAt".equals(sort)) {
            all = all.stream()
                    .sorted(Comparator.comparing(Alert::getOccurredAt).reversed())
                    .toList();
        }
        if (limit != null && limit >= 0 && limit < all.size()) {
            all = all.subList(0, limit);
        }
        if ("operational".equals(view)) {
            var resources = all.stream()
                    .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(resources);
        }
        var resources = all.stream()
                .filter(a -> a.getStatus() != AlertStatus.RESOLVED)
                .map(MobileAlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    // -------------------------------------------------------------------------
    // Single resource
    // -------------------------------------------------------------------------

    @GetMapping("/{alertId}")
    @Operation(
            summary = "Get alert by ID",
            description = "Returns the full detail of a single alert, including the enriched fields " +
                    "(driver name, vehicle code, vehicle type, incident description) computed by joining " +
                    "across the Trip, Driver, Vehicle, and Incident aggregates. " +
                    "The alert must belong to the authenticated company — ownership is enforced at the " +
                    "query service layer via companyId comparison.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert found and returned"),
            @ApiResponse(responseCode = "404", description = "Alert not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "Unique numeric identifier of the alert", required = true)
            @PathVariable("alertId") Long alertId) {
        var alert = alertQueryService.handle(new GetAlertByIdQuery(alertId));
        if (alert.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(AlertResourceFromEntityAssembler.toResourceFromEntity(alert.get()));
    }

    // -------------------------------------------------------------------------
    // Alert history (audit trail sub-resource)
    // -------------------------------------------------------------------------

    @GetMapping("/{alertId}/history")
    @Operation(
            summary = "Get alert history",
            description = "Returns the ordered audit trail of all actions taken on this alert " +
                    "(e.g. markReviewed, escalated, resolved). Each entry records the action type, " +
                    "the supervisor who performed it, and the timestamp. " +
                    "History entries are sourced from the AuditLogEntry table, filtered by alertId " +
                    "embedded in the JSON description parameters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert history returned (may be empty if no actions taken yet)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<AlertHistoryResource>> history(
            @Parameter(description = "Unique numeric identifier of the alert", required = true)
            @PathVariable("alertId") Long alertId) {
        if (alertQueryService.handle(new GetAlertByIdQuery(alertId)).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var token = "\"alertId\":" + alertId;
        var history = auditLogEntryRepository.findAll().stream()
                .filter(e -> e.getDescriptionParamsJson() != null && e.getDescriptionParamsJson().contains(token))
                .map(e -> new AlertHistoryResource(extract(e.getDescriptionParamsJson(), "action"),
                        extract(e.getDescriptionParamsJson(), "performedBy"), e.getOccurredAt()))
                .toList();
        return ResponseEntity.ok(history);
    }

    // -------------------------------------------------------------------------
    // Partial update (replaces PUT /operationalAlerts/{id} and the former
    // RPC-style POST /alerts/{id}/actions — both are now a single partial
    // update on the alert resource itself)
    // -------------------------------------------------------------------------

    @PatchMapping("/{alertId}")
    @Operation(
            summary = "Update alert",
            description = "Partially updates an existing alert: any subset of the editable fields " +
                    "(status, classification, notes) may be supplied — omitted fields are left unchanged. " +
                    "This is also how mobile/web clients act on an alert: sending `{\"status\": \"resolved\"}` " +
                    "or `{\"status\": \"false_alarm\"}` replaces the former RPC-style " +
                    "POST /alerts/{id}/actions endpoint. " +
                    "The alert must belong to the authenticated company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alert updated and returned"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Alert not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the alert to update", required = true)
            @PathVariable("alertId") Long alertId,
            @Valid @RequestBody UpdateAlertResource resource) {
        var command = UpdateAlertCommandFromResourceAssembler.toCommandFromResource(alertId, resource);
        var result = alertCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AlertResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private String extract(String json, String field) {
        var marker = "\"" + field + "\":\"";
        var start = json.indexOf(marker);
        if (start < 0) return "";
        start += marker.length();
        var end = json.indexOf('"', start);
        return end < 0 ? "" : json.substring(start, end);
    }
}
