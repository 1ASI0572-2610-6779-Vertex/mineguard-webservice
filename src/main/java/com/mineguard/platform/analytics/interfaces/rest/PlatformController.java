package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.AdminSummaryQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.AdminSummaryResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.AdminSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Formerly {@code AdminSummaryController} — renamed to align with the {@code /api/v1/platform} resource. */
@RestController
@RequestMapping(value = "/api/v1/platform", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Platform", description = "Cross-tenant platform administration resources. These endpoints are scoped " +
        "to users with the ADMIN or GLOBAL_ADMIN role and expose cross-company aggregations or platform-management " +
        "operations not available to regular supervisors.")
public class PlatformController {

    private final AdminSummaryQueryService queryService;

    public PlatformController(AdminSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/metrics")
    @Operation(
            summary = "Get platform-wide metrics",
            description = "Returns the global summary counters visible to a platform administrator: " +
                    "total registered companies, total active subscriptions, total users across all tenants, " +
                    "and global alert counts. " +
                    "Unlike the per-company KPIs (GET /api/v1/companies/{companyId}/kpis), " +
                    "this endpoint is NOT tenant-scoped — it aggregates across all companies. " +
                    "Requires ADMIN or GLOBAL_ADMIN role. Returns a single JSON object (not a list-wrapped " +
                    "singleton) since there is exactly one platform-wide summary. " +
                    "Formerly GET /admin/summary.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Platform metrics returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<AdminSummaryResource> getMetrics() {
        var resource = AdminSummaryResourceFromEntityAssembler.toResourceFromEntity(queryService.find());
        return ResponseEntity.ok(resource);
    }
}
