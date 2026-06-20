package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.VehicleCommandService;
import com.mineguard.platform.assets.application.queryservices.VehicleQueryService;
import com.mineguard.platform.assets.domain.model.queries.GetAllVehiclesQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateVehicleResource;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateVehicleResource;
import com.mineguard.platform.assets.interfaces.rest.resources.VehicleResource;
import com.mineguard.platform.assets.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import com.mineguard.platform.assets.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.mineguard.platform.assets.interfaces.rest.transform.VehicleResourceFromEntityAssembler;
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

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles", description = "Vehicle collection for this company. Consolidates the former /vehicles (mobile) " +
        "and /vehiclesInventory (web) endpoints: the same resource is served to both clients, " +
        "differentiated via the optional `view` query parameter.")
public class VehiclesInventoryController {

    private final VehicleQueryService vehicleQueryService;
    private final VehicleCommandService vehicleCommandService;

    public VehiclesInventoryController(VehicleQueryService vehicleQueryService,
                                       VehicleCommandService vehicleCommandService) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleCommandService = vehicleCommandService;
    }

    @GetMapping
    @Operation(
            summary = "List vehicles",
            description = "Returns all vehicles belonging to the authenticated company (tenant-isolated). " +
                    "Use the optional `view` query parameter to switch between payload shapes: " +
                    "`view=inventory` returns the enriched administration payload (formerly /vehiclesInventory); " +
                    "omitting `view` returns the compact mobile selection payload (formerly /vehicles).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<VehicleResource>> getAll(
            @Parameter(description = "Optional view variant. Accepted value: `inventory`")
            @RequestParam(required = false) String view) {
        var vehicles = vehicleQueryService.handle(new GetAllVehiclesQuery()).stream()
                .map(VehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    @Operation(
            summary = "Create a vehicle",
            description = "Registers a new vehicle in the company fleet. The vehicle is immediately available " +
                    "for driver check-in via POST /api/v1/vehicles/{vehicleId}/trips.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(@RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{vehicleId}")
    @Operation(
            summary = "Update a vehicle",
            description = "Replaces the full record of an existing vehicle. The vehicle must belong to the " +
                    "authenticated company. PUT is used because the administration form always submits all fields.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the vehicle to update", required = true)
            @PathVariable("vehicleId") Long vehicleId,
            @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(vehicleId, resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
