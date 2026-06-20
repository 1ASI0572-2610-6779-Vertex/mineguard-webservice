package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/trips/{tripId}/cardiac-readings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Trip Cardiac Readings", description = "Biometric heart-rate readings collected during an active Trip. " +
        "A CardiacReading is not a free-floating record — it is produced by a Sensor mounted on the Trip's Vehicle, " +
        "worn by the assigned Driver as a smart-band. The hierarchy is: " +
        "Trip → Vehicle → Sensor → SensorReading (heart_rate type) → CardiacReading (aggregated view). " +
        "Nesting under /trips/{tripId} makes the ownership chain explicit and prevents cross-tenant data access.")
public class CardiacReadingsController {

    private final CardiacReadingQueryService cardiacReadingQueryService;

    public CardiacReadingsController(CardiacReadingQueryService cardiacReadingQueryService) {
        this.cardiacReadingQueryService = cardiacReadingQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List cardiac readings for a trip",
            description = "Returns the latest heart-rate reading per driver for all drivers currently active in this trip. " +
                    "Resolution chain: the tripId identifies the Vehicle → the Vehicle has a mounted Sensor " +
                    "(type: smart-band) → the Sensor has SensorReadings of type `heart_rate` → " +
                    "only the most recent reading per driver is returned. " +
                    "Each reading is classified into a status: `normal` (< 110 bpm), `warning` (110–139 bpm), " +
                    "`critical` (≥ 140 bpm). A `critical` or `warning` reading may trigger an Alert automatically. " +
                    "All readings are filtered to the authenticated company's assets — cross-tenant " +
                    "access is blocked at the query service layer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cardiac readings returned (may be empty if no sensor data for this trip)"),
            @ApiResponse(responseCode = "404", description = "Trip not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<CardiacReadingResource>> getAll(
            @Parameter(description = "Unique numeric identifier of the trip (active driving shift)", required = true)
            @PathVariable("tripId") Long tripId) {
        var readings = cardiacReadingQueryService.handle(new GetAllCardiacReadingsQuery()).stream()
                .map(CardiacReadingResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(readings);
    }
}
