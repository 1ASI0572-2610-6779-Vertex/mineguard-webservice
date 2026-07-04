package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.TripCommandService;
import com.mineguard.platform.assets.domain.model.commands.CreateTripCommand;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateDrivingSessionResource;
import com.mineguard.platform.assets.interfaces.rest.transform.DrivingSessionResourceFromEntityAssembler;
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

/**
 * Driving Session management for a specific vehicle. In a mining operation, an operator does not
 * take a "trip" — they open a "driving session". This resource is DDD-renamed at the interface
 * layer only: the underlying persistence and application layer remain the {@code Trip} aggregate
 * ({@link TripCommandService}, {@code TripRepository}) — only the outward-facing contract changed.
 * Formerly {@code POST /vehicles/{vehicleId}/trips}.
 */
@RestController
@RequestMapping(value = "/api/v1/vehicles/{vehicleId}/driving-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Driving Sessions", description = "Driving-session management for a specific vehicle. A Driving Session " +
        "represents an active shift that binds a Driver to a Vehicle. It is the parent resource for Alerts and " +
        "CardiacReadings generated during that shift. Renamed from the former \"Trip\" terminology to match the " +
        "mining domain's ubiquitous language — the underlying data model is unchanged.")
public class VehicleDrivingSessionsController {

    private final TripCommandService tripCommandService;

    public VehicleDrivingSessionsController(TripCommandService tripCommandService) {
        this.tripCommandService = tripCommandService;
    }

    @PostMapping
    @Operation(
            summary = "Start a driving session (operator check-in)",
            description = "Creates a new Driving Session with status IN_PROGRESS, binding the specified driver to " +
                    "this vehicle. This is the check-in action performed by the operator from the mobile app. " +
                    "Business rules enforced: (1) the vehicle must exist and belong to the authenticated company; " +
                    "(2) the driver must exist and belong to the same company; " +
                    "(3) the vehicle must not already have another Driving Session in progress — checking in " +
                    "twice on the same vehicle is a state conflict (409), not a validation error; " +
                    "(4) companyId is automatically set from the JWT — it is never supplied by the client. " +
                    "Alerts and CardiacReadings generated after check-in are nested under this session's ID: " +
                    "GET /api/v1/driving-sessions/{sessionId}/cardiac-readings. " +
                    "Formerly POST /api/v1/vehicles/{vehicleId}/trips.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Driving session started — check-in successful"),
            @ApiResponse(responseCode = "400", description = "Invalid driverId or malformed request body"),
            @ApiResponse(responseCode = "404", description = "Vehicle or driver not found, or not accessible by this tenant"),
            @ApiResponse(responseCode = "409", description = "The vehicle already has an active Driving Session in progress"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(
            @Parameter(description = "Unique numeric identifier of the vehicle being checked into", required = true)
            @PathVariable("vehicleId") Long vehicleId,
            @Valid @RequestBody CreateDrivingSessionResource resource) {
        var command = new CreateTripCommand(vehicleId, resource.driverId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                tripCommandService.handle(command),
                DrivingSessionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}
