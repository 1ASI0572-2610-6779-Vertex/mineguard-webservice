package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.queryservices.CatalogSummaryQueryService;
import com.mineguard.platform.assets.domain.model.queries.GetCatalogSummaryQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CatalogSummaryResource;
import com.mineguard.platform.assets.interfaces.rest.transform.CatalogSummaryResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Catalog", description = "Assets catalog for this company. The catalog aggregates counts and metadata " +
        "of drivers, vehicles, and related assets — used as a header widget in the administration panel.")
public class CatalogSummaryController {

    private final CatalogSummaryQueryService catalogSummaryQueryService;

    public CatalogSummaryController(CatalogSummaryQueryService catalogSummaryQueryService) {
        this.catalogSummaryQueryService = catalogSummaryQueryService;
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Get catalog summary",
            description = "Returns an aggregated summary of the company's asset catalog: total drivers, total vehicles, " +
                    "active trips, and similar counters. " +
                    "The response is wrapped in a single-element array to maintain compatibility with the frontend " +
                    "widget contract (originally designed for a json-server mock that always returned arrays).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catalog summary returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<CatalogSummaryResource>> getSummary() {
        var summary = catalogSummaryQueryService.handle(new GetCatalogSummaryQuery())
                .map(CatalogSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
        return ResponseEntity.ok(summary);
    }
}
