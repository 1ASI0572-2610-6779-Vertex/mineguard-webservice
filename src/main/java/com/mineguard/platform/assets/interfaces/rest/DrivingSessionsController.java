package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.TripCommandService;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateDrivingSessionResource;
import com.mineguard.platform.assets.interfaces.rest.transform.DrivingSessionResourceFromEntityAssembler;
import com.mineguard.platform.assets.interfaces.rest.transform.UpdateTripCommandFromResourceAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lifecycle operations on a Driving Session addressed by its own ID, independent of the vehicle
 * it was opened under. Complements {@link VehicleDrivingSessionsController}, which only handles
 * the check-in (creation) under {@code /vehicles/{vehicleId}/driving-sessions}.
 */
@RestController
@RequestMapping(value = "/api/v1/driving-sessions", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Driving Sessions", description = "Driving-session management for a specific vehicle. A Driving Session " +
        "represents an active shift that binds a Driver to a Vehicle. It is the parent resource for Alerts and " +
        "CardiacReadings generated during that shift.")
public class DrivingSessionsController {

    private final TripCommandService tripCommandService;

    public DrivingSessionsController(TripCommandService tripCommandService) {
        this.tripCommandService = tripCommandService;
    }

    @PatchMapping("/{sessionId}")
    @Operation(
            summary = "Close or cancel a driving session (check-out)",
            description = "Performs a partial update of a Driving Session. Setting `status` to `COMPLETED` " +
                    "(normal check-out) or `CANCELLED` closes the session: the server stamps `endTime` with the " +
                    "current time and, once persisted, this session becomes eligible for PerformanceMetric " +
                    "calculation. Business rules enforced: " +
                    "(1) the session must belong to the authenticated company — otherwise `404`; " +
                    "(2) the session must currently be `IN_PROGRESS` — closing an already-closed session is a " +
                    "state conflict (`409`), not a validation error.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driving session updated (closed or cancelled)"),
            @ApiResponse(responseCode = "400", description = "Malformed status value"),
            @ApiResponse(responseCode = "404", description = "Driving session not found or not accessible by this tenant"),
            @ApiResponse(responseCode = "409", description = "Driving session is already closed"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the driving session", required = true)
            @PathVariable("sessionId") Long sessionId,
            @RequestBody UpdateDrivingSessionResource resource) {
        var command = UpdateTripCommandFromResourceAssembler.toCommandFromResource(sessionId, resource);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                tripCommandService.handle(command),
                DrivingSessionResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }
}
