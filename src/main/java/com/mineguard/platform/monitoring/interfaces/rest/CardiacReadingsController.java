package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetCardiacReadingQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.CardiacReadingResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.CardiacReadingResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/driving-sessions/{sessionId}/cardiac-readings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Driving Session Cardiac Readings", description = "Biometric heart-rate reading for an active Driving " +
        "Session. A CardiacReading is not a free-floating record — it is produced by a Sensor mounted on the " +
        "session's Vehicle, worn by the assigned Driver as a smart-band. The hierarchy is: " +
        "DrivingSession → Vehicle → Sensor → SensorReading (heart_rate type) → CardiacReading (single aggregated " +
        "value). Nesting under /driving-sessions/{sessionId} makes the ownership chain explicit and prevents " +
        "cross-tenant data access. Formerly nested under /trips/{tripId} — the underlying data model (Trip " +
        "aggregate) is unchanged.")
public class CardiacReadingsController {

    private final CardiacReadingQueryService cardiacReadingQueryService;

    public CardiacReadingsController(CardiacReadingQueryService cardiacReadingQueryService) {
        this.cardiacReadingQueryService = cardiacReadingQueryService;
    }

    @GetMapping
    @Operation(
            summary = "Get the cardiac reading for a driving session",
            description = "Returns the single latest heart-rate reading for this driving session — a session has " +
                    "exactly one active driver, so the resource is a singleton, not a collection. " +
                    "Resolution chain: the sessionId identifies the Vehicle → the Vehicle has a mounted Sensor " +
                    "(type: smart-band) → the Sensor has SensorReadings of type `heart_rate` → the most recent " +
                    "one is returned. " +
                    "Classified into a status: `normal` (< 110 bpm), `warning` (110–139 bpm), " +
                    "`critical` (≥ 140 bpm). A `critical` or `warning` reading may trigger an Alert automatically. " +
                    "Filtered to the authenticated company's assets — cross-tenant access is blocked at the " +
                    "query service layer. " +
                    "Formerly returned a `List<CardiacReadingResource>` of 0 or 1 elements; now returns the " +
                    "object directly (200) or 404 if no reading exists yet for this session.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiac reading returned"),
            @ApiResponse(responseCode = "404", description = "Driving session not found, does not belong to this tenant, or has no sensor data yet"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<CardiacReadingResource> getOne(
            @Parameter(description = "Unique numeric identifier of the driving session (active shift)", required = true)
            @PathVariable("sessionId") Long sessionId) {
        return cardiacReadingQueryService.handle(new GetCardiacReadingQuery(sessionId))
                .map(CardiacReadingResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
