package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.SensorCommandService;
import com.mineguard.platform.monitoring.application.queryservices.SensorQueryService;
import com.mineguard.platform.monitoring.domain.model.commands.LinkDeviceCommand;
import com.mineguard.platform.monitoring.interfaces.rest.resources.SensorResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.SensorResourceFromEntityAssembler;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Nested device resource for a vehicle: a MineGuard device is 1:1 with a vehicle and modelled as a
 * "sensor". This is the single-flow entry point the supervisor web panel uses to provision the
 * device right after creating the vehicle. Both operations are restricted to Administrator and
 * Supervisor and are always scoped to the caller's own tenant (resolved from the JWT).
 */
@RestController
@RequestMapping(value = "/api/v1/vehicles/{vehicleId}/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
@Tag(name = "Vehicle Device", description = "Link and read the single MineGuard device (sensor) mounted on a vehicle.")
public class VehicleSensorController {

    private final SensorCommandService sensorCommandService;
    private final SensorQueryService sensorQueryService;
    private final SecurityContextFacade securityContext;

    public VehicleSensorController(SensorCommandService sensorCommandService, SensorQueryService sensorQueryService,
                                   SecurityContextFacade securityContext) {
        this.sensorCommandService = sensorCommandService;
        this.sensorQueryService = sensorQueryService;
        this.securityContext = securityContext;
    }

    @PostMapping
    @Operation(
            summary = "Link a MineGuard device to a vehicle",
            description = "Provisions the vehicle's single device. The request body is empty on purpose — the " +
                    "server assigns the next sequential per-company `deviceId` (an integer, serialized as a " +
                    "string), fixes `sensorType` to \"MineGuard Device\" and the initial `status` to \"active\". " +
                    "The vehicle must belong to the caller's tenant; a vehicle that already has a device is rejected.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device linked — returns the created device"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "409", description = "The vehicle already has a device linked"),
            @ApiResponse(responseCode = "403", description = "Access denied — Administrator or Supervisor role required")
    })
    public ResponseEntity<?> link(
            @Parameter(description = "Unique numeric identifier of the vehicle", required = true)
            @PathVariable("vehicleId") Long vehicleId) {
        var command = new LinkDeviceCommand(vehicleId, securityContext.currentCompanyId());
        return ResponseEntityAssembler.toResponseEntityFromResult(sensorCommandService.handle(command),
                SensorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get the vehicle's device",
            description = "Returns the device (sensor) currently mounted on the vehicle. Feeds the inventory " +
                    "\"without device\" filter. Returns 404 when the vehicle has no device (or is not in this tenant).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device returned"),
            @ApiResponse(responseCode = "404", description = "The vehicle has no device, or is not in this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — Administrator or Supervisor role required")
    })
    public ResponseEntity<SensorResource> get(
            @Parameter(description = "Unique numeric identifier of the vehicle", required = true)
            @PathVariable("vehicleId") Long vehicleId) {
        return sensorQueryService.findActiveByVehicleForCurrentCompany(vehicleId)
                .map(SensorResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
