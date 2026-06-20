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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Admin", description = "Administrative panel resources. These endpoints are scoped to users with " +
        "the ADMIN or GLOBAL_ADMIN role and expose cross-company aggregations or company-management operations " +
        "not available to regular supervisors.")
public class AdminSummaryController {

    private final AdminSummaryQueryService queryService;

    public AdminSummaryController(AdminSummaryQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get admin summary",
            description = "Returns the global summary counters visible to a platform administrator: " +
                    "total registered companies, total active subscriptions, total users across all tenants, " +
                    "and global alert counts. " +
                    "Unlike the supervisor dashboard summary (GET /api/v1/dashboard/summary), " +
                    "this endpoint is NOT tenant-scoped — it aggregates across all companies. " +
                    "Requires ADMIN or GLOBAL_ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin summary returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — ADMIN role required")
    })
    public ResponseEntity<List<AdminSummaryResource>> getSummary() {
        var items = queryService.findAll().stream()
                .map(AdminSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }
}
