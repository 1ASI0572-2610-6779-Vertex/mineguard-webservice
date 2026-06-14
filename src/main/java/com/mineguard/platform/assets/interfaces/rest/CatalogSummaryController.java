package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.queryservices.CatalogSummaryQueryService;
import com.mineguard.platform.assets.domain.model.queries.GetCatalogSummaryQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CatalogSummaryResource;
import com.mineguard.platform.assets.interfaces.rest.transform.CatalogSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Assets catalog summary endpoint ({@code /catalogSummary}, array-of-one to match json-server). */
@RestController
@RequestMapping(value = "/catalogSummary", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Catalog Summary", description = "Assets catalog summary widget")
public class CatalogSummaryController {
    private final CatalogSummaryQueryService catalogSummaryQueryService;

    public CatalogSummaryController(CatalogSummaryQueryService catalogSummaryQueryService) {
        this.catalogSummaryQueryService = catalogSummaryQueryService;
    }

    @GetMapping
    public ResponseEntity<List<CatalogSummaryResource>> get() {
        var summary = catalogSummaryQueryService.handle(new GetCatalogSummaryQuery())
                .map(CatalogSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
        return ResponseEntity.ok(summary);
    }
}
