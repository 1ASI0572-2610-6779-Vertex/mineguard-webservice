package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.DriverCommandService;
import com.mineguard.platform.assets.application.queryservices.DriverQueryService;
import com.mineguard.platform.assets.domain.model.commands.CreateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateDriverCommand;
import com.mineguard.platform.assets.domain.model.queries.GetAllDriversQuery;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByIdQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateDriverResource;
import com.mineguard.platform.assets.interfaces.rest.resources.DriverResource;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateDriverResource;
import com.mineguard.platform.assets.interfaces.rest.transform.DriverResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/drivers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Drivers", description = "Driver management and directory. A Driver is an operator registered under a Company who can be assigned to a Vehicle via a Trip (check-in).")
public class DriversController {

    private final DriverCommandService driverCommandService;
    private final DriverQueryService driverQueryService;

    public DriversController(DriverCommandService driverCommandService, DriverQueryService driverQueryService) {
        this.driverCommandService = driverCommandService;
        this.driverQueryService = driverQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List drivers",
            description = "Returns all drivers belonging to the authenticated company (tenant-isolated). " +
                    "Use the optional `view` query parameter to switch between representations: " +
                    "`view=directory` returns the compact directory payload used by the supervisor web panel " +
                    "(previously served by the separate /driversDirectory endpoint, now consolidated here).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DriverResource>> getAll(
            @Parameter(description = "Optional view variant. Accepted value: `directory`")
            @RequestParam(required = false) String view) {
        var drivers = driverQueryService.handle(new GetAllDriversQuery()).stream()
                .map(DriverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{driverId}")
    @Operation(
            summary = "Get driver by ID",
            description = "Returns the full profile of a single driver. The driver must belong to the " +
                    "authenticated company — ownership is enforced at the query service layer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver found and returned"),
            @ApiResponse(responseCode = "404", description = "Driver not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> getById(
            @Parameter(description = "Unique numeric identifier of the driver", required = true)
            @PathVariable("driverId") Long driverId) {
        var driver = driverQueryService.handle(new GetDriverByIdQuery(driverId));
        if (driver.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(DriverResourceFromEntityAssembler.toResourceFromEntity(driver.get()));
    }

    @PostMapping
    @Operation(
            summary = "Create a driver",
            description = "Registers a new driver under the specified company. Internally creates a linked " +
                    "IAM User with role DRIVER and generates a workerId in the format `CDT-{companyId}-{seq}` " +
                    "which the driver uses to log in from the mobile app.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Driver created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(@RequestBody CreateDriverResource resource) {
        var command = new CreateDriverCommand(resource.username(), resource.password(), resource.email(),
                resource.fullName(), resource.idCompany(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{driverId}")
    @Operation(
            summary = "Update a driver",
            description = "Replaces the full profile of an existing driver. The driver must belong to the " +
                    "authenticated company. A full replacement (PUT) is used because all editable fields " +
                    "are always submitted together from the administration form.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Driver not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the driver to update", required = true)
            @PathVariable("driverId") Long driverId,
            @RequestBody UpdateDriverResource resource) {
        var command = new UpdateDriverCommand(driverId, resource.username(), resource.password(), resource.email(),
                resource.fullName(), resource.idCompany(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
