package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.SensorCommandService;
import com.mineguard.platform.monitoring.application.queryservices.SensorQueryService;
import com.mineguard.platform.monitoring.domain.model.commands.CreateSensorCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateSensorCommand;
import com.mineguard.platform.monitoring.interfaces.rest.resources.CreateSensorResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.SensorResource;
import com.mineguard.platform.monitoring.interfaces.rest.resources.UpdateSensorResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.SensorResourceFromEntityAssembler;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
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

/**
 * Sensor provisioning for the web admin panel. A Sensor links a physical edge device
 * (identified by its {@code deviceId}) to a Vehicle within the authenticated company.
 * This is the record the IoT telemetry pipeline resolves at ingestion time:
 * {@code POST /api/v1/telemetry} looks up the sensor by {@code (device_id, companyId)} to
 * discover which vehicle the reading belongs to. Until a sensor is registered here, telemetry
 * for that device is rejected with 404.
 */
@RestController
@RequestMapping(value = "/api/v1/sensors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Sensors", description = "Provision and list sensors (edge devices) mounted on the company's vehicles.")
public class SensorsController {

    private final SensorCommandService sensorCommandService;
    private final SensorQueryService sensorQueryService;
    private final SecurityContextFacade securityContext;

    public SensorsController(SensorCommandService sensorCommandService, SensorQueryService sensorQueryService,
                             SecurityContextFacade securityContext) {
        this.sensorCommandService = sensorCommandService;
        this.sensorQueryService = sensorQueryService;
        this.securityContext = securityContext;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "List sensors",
            description = "Returns all sensors owned by the authenticated company (tenant-isolated). " +
                    "Read-only audit collection restricted to Administrators.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sensor list returned"),
            @ApiResponse(responseCode = "403", description = "Access denied — Administrator role required")
    })
    public ResponseEntity<List<SensorResource>> getAll() {
        var sensors = sensorQueryService.findAllForCurrentCompany().stream()
                .map(SensorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(sensors);
    }

    @PostMapping
    @Operation(
            summary = "Register a sensor",
            description = "Provisions a new sensor and mounts it on one of the company's vehicles. The " +
                    "`deviceId` is the identifier the physical edge device sends as `device_id` in its " +
                    "telemetry payloads — it must be unique within the company. The owning company is " +
                    "always the caller's own tenant (resolved from the JWT) and cannot be supplied in the body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sensor registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "409", description = "A sensor with this deviceId already exists for the company"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(@Valid @RequestBody CreateSensorResource resource) {
        var command = new CreateSensorCommand(resource.vehicleId(), resource.sensorType(),
                resource.deviceId(), resource.status(), securityContext.currentCompanyId());
        return ResponseEntityAssembler.toResponseEntityFromResult(sensorCommandService.handle(command),
                SensorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR')")
    @Operation(
            summary = "Update a device (move and/or change status)",
            description = "Partially updates a device. Supply `vehicleId` to move it to another vehicle — the " +
                    "device keeps its `deviceId` (the integer that indexes the embedded unit); the target vehicle " +
                    "must belong to the tenant and must not already have a device. Supply `status` " +
                    "(`active` | `inactive` | `retired`) to change its lifecycle state; `retired` reserves the id " +
                    "permanently and drops the device from the active/total sensor KPIs. Both may be sent together; " +
                    "omitted fields are left unchanged.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated — returns the device"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Device or target vehicle not found in this tenant"),
            @ApiResponse(responseCode = "409", description = "The target vehicle already has a device"),
            @ApiResponse(responseCode = "403", description = "Access denied — Administrator or Supervisor role required")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the device (sensor)", required = true)
            @PathVariable("id") Long id,
            @RequestBody UpdateSensorResource resource) {
        var command = new UpdateSensorCommand(id, resource.vehicleId(), resource.status(),
                securityContext.currentCompanyId());
        return ResponseEntityAssembler.toResponseEntityFromResult(sensorCommandService.handle(command),
                SensorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}