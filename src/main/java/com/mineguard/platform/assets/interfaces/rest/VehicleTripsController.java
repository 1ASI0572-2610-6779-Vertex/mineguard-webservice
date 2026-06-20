package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.TripCommandService;
import com.mineguard.platform.assets.domain.model.commands.CreateTripCommand;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateTripResource;
import com.mineguard.platform.assets.interfaces.rest.transform.TripResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/vehicles/{vehicleId}/trips", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicle Trips", description = "Trip management for a specific vehicle. A Trip represents an active " +
        "driving shift that binds a Driver to a Vehicle. It is the parent resource for Alerts and " +
        "CardiacReadings generated during that shift.")
public class VehicleTripsController {

    private final TripCommandService tripCommandService;

    public VehicleTripsController(TripCommandService tripCommandService) {
        this.tripCommandService = tripCommandService;
    }

    @PostMapping
    @Operation(
            summary = "Start a trip (driver check-in)",
            description = "Creates a new Trip with status IN_PROGRESS, binding the specified driver to this vehicle. " +
                    "This is the check-in action performed by the driver from the mobile app. " +
                    "Business rules enforced: (1) the vehicle must exist and belong to the authenticated company; " +
                    "(2) the driver must exist and belong to the same company; " +
                    "(3) companyId is automatically set from the JWT — it is never supplied by the client. " +
                    "Alerts and CardiacReadings generated after check-in are nested under this trip's ID: " +
                    "GET /api/v1/vehicles/{vehicleId}/trips/{tripId}/alerts and " +
                    "GET /api/v1/vehicles/{vehicleId}/trips/{tripId}/cardiac-readings.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trip started — check-in successful"),
            @ApiResponse(responseCode = "400", description = "Invalid driverId or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Vehicle or driver not found, or not accessible by this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(
            @Parameter(description = "Unique numeric identifier of the vehicle being checked into", required = true)
            @PathVariable("vehicleId") Long vehicleId,
            @RequestBody CreateTripResource resource) {
        var command = new CreateTripCommand(vehicleId, resource.driverId());
        return ResponseEntityAssembler.toResponseEntityFromResult(
                tripCommandService.handle(command),
                TripResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }
}
