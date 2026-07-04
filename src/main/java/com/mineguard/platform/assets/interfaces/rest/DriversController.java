package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.analytics.application.queryservices.DashboardRiskDriverQueryService;
import com.mineguard.platform.analytics.domain.model.aggregates.DashboardRiskDriver;
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
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/drivers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Drivers", description = "Driver management and directory. A Driver is an operator registered under a Company who can be assigned to a Vehicle via a Trip (check-in).")
public class DriversController {

    private final DriverCommandService driverCommandService;
    private final DriverQueryService driverQueryService;
    private final DashboardRiskDriverQueryService riskDriverQueryService;
    private final SecurityContextFacade securityContext;

    public DriversController(DriverCommandService driverCommandService, DriverQueryService driverQueryService,
                             DashboardRiskDriverQueryService riskDriverQueryService,
                             SecurityContextFacade securityContext) {
        this.driverCommandService = driverCommandService;
        this.driverQueryService = driverQueryService;
        this.riskDriverQueryService = riskDriverQueryService;
        this.securityContext = securityContext;
    }

    @GetMapping
    @Operation(
            summary = "List drivers",
            description = "Returns all drivers belonging to the authenticated company (tenant-isolated). " +
                    "Use the optional `view` query parameter to switch between representations: " +
                    "`view=directory` returns the compact directory payload used by the supervisor web panel " +
                    "(previously served by the separate /driversDirectory endpoint, now consolidated here). " +
                    "Pass `sort=-riskScore` to rank drivers by current risk score descending (replaces the " +
                    "former GET /dashboard/risk-drivers widget endpoint) and `limit=N` to cap the result size " +
                    "— e.g. `GET /api/v1/drivers?sort=-riskScore&limit=5` for the 'At-Risk Drivers' widget. " +
                    "Sorting never changes the response shape: every `DriverResource` field is always populated " +
                    "the same way regardless of `sort` — only the ordering (and, with `limit`, the count) changes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<DriverResource>> getAll(
            @Parameter(description = "Optional view variant. Accepted value: `directory`")
            @RequestParam(required = false) String view,
            @Parameter(description = "Optional sort key. Accepted value: `-riskScore` (descending)")
            @RequestParam(required = false) String sort,
            @Parameter(description = "Optional maximum number of results to return")
            @RequestParam(required = false) Integer limit) {
        var allDrivers = driverQueryService.handle(new GetAllDriversQuery());
        List<DriverResource> drivers;
        if ("-riskScore".equals(sort)) {
            Map<Long, Double> riskScoreByDriverId = riskDriverQueryService.findAll().stream()
                    .collect(Collectors.toMap(DashboardRiskDriver::getDriverId, DashboardRiskDriver::getRiskScore));
            drivers = allDrivers.stream()
                    .map(d -> DriverResourceFromEntityAssembler.toResourceFromEntity(d, riskScoreByDriverId.get(d.getId())))
                    .sorted(Comparator.comparing(
                            (DriverResource r) -> r.riskScore() == null ? Double.NEGATIVE_INFINITY : r.riskScore())
                            .reversed())
                    .toList();
        } else {
            drivers = allDrivers.stream()
                    .map(DriverResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
        }
        if (limit != null && limit >= 0 && limit < drivers.size()) {
            drivers = drivers.subList(0, limit);
        }
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
            description = "Registers a new driver under the authenticated company. Internally creates a linked " +
                    "IAM User with role DRIVER, generating both the workerId (format `CDT-{companyId}-{seq}`) and " +
                    "a temporary password server-side. The request body has no `username`/`password` fields — " +
                    "the schema does not accept credentials on creation, and any unrecognized field is rejected " +
                    "with 400. " +
                    "The company is always the caller's own tenant, resolved from the JWT — it cannot be " +
                    "supplied in the request body.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Driver created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> create(@Valid @RequestBody CreateDriverResource resource) {
        var command = new CreateDriverCommand(resource.email(),
                resource.fullName(), securityContext.currentCompanyId(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PatchMapping("/{driverId}")
    @Operation(
            summary = "Update a driver",
            description = "Partially updates an existing driver: any subset of the editable fields may be " +
                    "supplied — fields omitted (or absent from the JSON body) are left unchanged. " +
                    "The driver must belong to the authenticated company.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Driver updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Driver not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the driver to update", required = true)
            @PathVariable("driverId") Long driverId,
            @Valid @RequestBody UpdateDriverResource resource) {
        var command = new UpdateDriverCommand(driverId, resource.username(), resource.password(), resource.email(),
                resource.fullName(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
