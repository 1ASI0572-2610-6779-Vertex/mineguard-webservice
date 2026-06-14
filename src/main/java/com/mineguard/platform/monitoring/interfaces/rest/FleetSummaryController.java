package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.FleetSummaryQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetFleetSummaryQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.FleetSummaryResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.FleetSummaryResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Web fleet summary endpoint ({@code /fleetSummary}, array-of-one). */
@RestController
@RequestMapping(value = "/fleetSummary", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Summary", description = "Aggregated fleet status")
public class FleetSummaryController {
    private final FleetSummaryQueryService fleetSummaryQueryService;

    public FleetSummaryController(FleetSummaryQueryService fleetSummaryQueryService) {
        this.fleetSummaryQueryService = fleetSummaryQueryService;
    }

    @GetMapping
    public ResponseEntity<List<FleetSummaryResource>> get() {
        var summary = fleetSummaryQueryService.handle(new GetFleetSummaryQuery())
                .map(FleetSummaryResourceFromEntityAssembler::toResourceFromEntity)
                .map(List::of)
                .orElseGet(List::of);
        return ResponseEntity.ok(summary);
    }
}
