package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.VehicleCommandService;
import com.mineguard.platform.assets.application.queryservices.VehicleQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.commands.ArchiveVehicleCommand;
import com.mineguard.platform.assets.domain.model.queries.GetAllVehiclesQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateVehicleResource;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateVehicleResource;
import com.mineguard.platform.assets.interfaces.rest.resources.VehicleResource;
import com.mineguard.platform.assets.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import com.mineguard.platform.assets.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.mineguard.platform.assets.interfaces.rest.transform.VehicleResourceFromEntityAssembler;
import com.mineguard.platform.monitoring.application.queryservices.SensorQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.interfaces.rest.transform.ErrorResponseAssembler;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles", description = "Vehicle collection for this company. Consolidates the former /vehicles (mobile) " +
        "and /vehiclesInventory (web) endpoints: the same resource is served to both clients, " +
        "differentiated via the optional `view` query parameter.")
public class VehiclesInventoryController {

    private final VehicleQueryService vehicleQueryService;
    private final VehicleCommandService vehicleCommandService;
    private final SensorQueryService sensorQueryService;

    public VehiclesInventoryController(VehicleQueryService vehicleQueryService,
                                       VehicleCommandService vehicleCommandService,
                                       SensorQueryService sensorQueryService) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleCommandService = vehicleCommandService;
        this.sensorQueryService = sensorQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List vehicles",
            description = "Returns all vehicles belonging to the authenticated company (tenant-isolated). " +
                    "Use the optional `view` query parameter to switch between payload shapes: " +
                    "`view=inventory` returns the enriched administration payload (formerly /vehiclesInventory); " +
                    "omitting `view` returns the compact mobile selection payload (formerly /vehicles). " +
                    "Each vehicle carries its linked device id as `deviceId` (or `null` when it has none). " +
                    "Archived (decommissioned) vehicles are excluded by default; pass `includeArchived=true` " +
                    "to include them for audit.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<VehicleResource>> getAll(
            @Parameter(description = "Optional view variant. Accepted value: `inventory`")
            @RequestParam(required = false) String view,
            @Parameter(description = "Include archived (decommissioned) vehicles. Defaults to false.")
            @RequestParam(required = false, defaultValue = "false") boolean includeArchived) {
        // Map each vehicle to the id of its non-retired device (retired hardware does not count as linked).
        Map<Long, String> deviceIdByVehicleId = sensorQueryService.findAllForCurrentCompany().stream()
                .filter(s -> !s.isRetired() && s.getVehicleId() != null && s.getDeviceId() != null)
                .collect(Collectors.toMap(Sensor::getVehicleId, Sensor::getDeviceId, (a, b) -> a));
        var vehicles = vehicleQueryService.handle(new GetAllVehiclesQuery()).stream()
                .filter(v -> includeArchived || !v.isArchived())
                .map(v -> VehicleResourceFromEntityAssembler.toResourceFromEntity(v, deviceIdByVehicleId.get(v.getId())))
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    @Operation(
            summary = "Create a vehicle",
            description = "Registers a new vehicle in the company fleet. The vehicle is immediately available " +
                    "for driver check-in via POST /api/v1/vehicles/{vehicleId}/driving-sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(@Valid @RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{vehicleId}")
    @Operation(
            summary = "Update a vehicle",
            description = "Partially updates an existing vehicle: any subset of the editable fields may be " +
                    "supplied — fields omitted (or absent from the JSON body) are left unchanged. " +
                    "The vehicle must belong to the authenticated company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the vehicle to update", required = true)
            @PathVariable("vehicleId") Long vehicleId,
            @Valid @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(vehicleId, resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
    @Operation(
            summary = "Decommission (archive) a vehicle",
            description = "Soft-deletes the vehicle: it is marked as archived, never physically removed, so its " +
                    "incident/telemetry/session history is preserved. The vehicle is then excluded from the " +
                    "inventory by default. Rejected with 409 while the vehicle still has an active device — the " +
                    "device must be moved (PATCH /api/v1/sensors/{id}) or retired first.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle archived"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "409", description = "The vehicle still has an active device linked"),
            @ApiResponse(responseCode = "403", description = "Access denied — Administrator or Supervisor role required")
    })
    public ResponseEntity<?> archive(
            @Parameter(description = "Unique numeric identifier of the vehicle to archive", required = true)
            @PathVariable("vehicleId") Long vehicleId) {
        Result<Vehicle, ApplicationError> result = vehicleCommandService.handle(new ArchiveVehicleCommand(vehicleId));
        return switch (result) {
            case Result.Success<Vehicle, ApplicationError> ignored -> ResponseEntity.noContent().build();
            case Result.Failure<Vehicle, ApplicationError> failure ->
                    ErrorResponseAssembler.toErrorResponseFromApplicationError(failure.error());
        };
    }
}
