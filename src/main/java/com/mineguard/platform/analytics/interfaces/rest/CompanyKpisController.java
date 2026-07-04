package com.mineguard.platform.analytics.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.CompanyKpisQueryService;
import com.mineguard.platform.analytics.interfaces.rest.resources.CompanyKpisResource;
import com.mineguard.platform.analytics.interfaces.rest.transform.CompanyKpisResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consolidates the former GET /dashboard/summary, GET /fleet/summary, and GET /catalog/summary
 * widget endpoints into a single KPI resource nested under its owning company — a computed
 * projection is still a resource, and the company is the natural parent for fleet-wide counters.
 */
@RestController
@RequestMapping(value = "/api/v1/companies/{companyId}/kpis", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Company KPIs", description = "Aggregated fleet, catalog, and safety counters for a single company. " +
        "Replaces the former /dashboard/summary, /fleet/summary, and /catalog/summary widget endpoints, " +
        "which duplicated overlapping counters under three different paths.")
public class CompanyKpisController {

    private final CompanyKpisQueryService queryService;

    public CompanyKpisController(CompanyKpisQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    @Operation(
            summary = "Get company KPIs",
            description = "Returns the consolidated fleet/catalog/dashboard counters for the given company: " +
                    "driver and vehicle counts by status, supervisor counts, sensor health, critical alerts, " +
                    "and fatigue events. " +
                    "The {companyId} path variable must match the authenticated caller's own tenant — " +
                    "requesting another company's KPIs returns 404, never another tenant's data.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company KPIs returned successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found or does not belong to the authenticated tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<CompanyKpisResource> getKpis(
            @Parameter(description = "Unique numeric identifier of the company", required = true)
            @PathVariable("companyId") Long companyId) {
        return queryService.findForCompany(companyId)
                .map(CompanyKpisResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
