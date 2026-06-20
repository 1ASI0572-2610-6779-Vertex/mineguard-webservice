package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.LiveMapVehicleQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllLiveMapVehiclesQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.LiveMapVehicleResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.LiveMapVehicleResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/vehicles/live-positions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicle Live Positions", description = "Real-time GPS positions of the company fleet. " +
        "Live position data is a state sub-resource of Vehicle: each entry enriches the vehicle record " +
        "with current coordinates, speed, heading, and connection status as reported by the on-board GPS unit. " +
        "The path /vehicles/live-positions models this as a specialized sub-collection of the vehicles resource.")
public class LiveMapVehiclesController {

    private final LiveMapVehicleQueryService liveMapVehicleQueryService;

    public LiveMapVehiclesController(LiveMapVehicleQueryService liveMapVehicleQueryService) {
        this.liveMapVehicleQueryService = liveMapVehicleQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List live positions for all fleet vehicles",
            description = "Returns the latest GPS position snapshot for every vehicle belonging to the " +
                    "authenticated company. Used by the real-time map panel in the supervisor web app. " +
                    "LiveMapVehicle records do not carry a companyId themselves — tenant isolation is enforced " +
                    "by matching vehicle codes against the authenticated company's vehicle registry " +
                    "(cross-reference performed in LiveMapVehicleQueryServiceImpl). " +
                    "Vehicles with no recent GPS report may be absent from the list.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Live positions returned (may be empty if no vehicles are reporting)"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<LiveMapVehicleResource>> getAll() {
        var vehicles = liveMapVehicleQueryService.handle(new GetAllLiveMapVehiclesQuery()).stream()
                .map(LiveMapVehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicles);
    }
}
