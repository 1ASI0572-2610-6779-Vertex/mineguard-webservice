package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.iam.application.commandservices.SupervisorCommandService;
import com.mineguard.platform.iam.application.queryservices.SupervisorQueryService;
import com.mineguard.platform.iam.domain.model.queries.GetAllSupervisorsQuery;
import com.mineguard.platform.iam.interfaces.rest.resources.*;
import com.mineguard.platform.iam.interfaces.rest.transform.CreateSupervisorCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.SupervisorResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.UpdateSupervisorCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/supervisors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supervisors", description = "Supervisor account administration for the authenticated company. " +
        "A Supervisor is a User with role SUPERVISOR, linked to a specific Company. " +
        "Supervisors access the web panel to monitor the fleet, review alerts, and manage drivers. " +
        "All endpoints require a valid JWT with SUPERVISOR or ADMIN role.")
public class SupervisorsController {

    private final SupervisorCommandService supervisorCommandService;
    private final SupervisorQueryService supervisorQueryService;

    public SupervisorsController(SupervisorCommandService supervisorCommandService,
                                  SupervisorQueryService supervisorQueryService) {
        this.supervisorCommandService = supervisorCommandService;
        this.supervisorQueryService = supervisorQueryService;
    }

    @GetMapping
    @Operation(
            summary = "List supervisors",
            description = "Returns all supervisor accounts belonging to the authenticated company. " +
                    "The response is a flat array (not paginated) for compatibility with the web panel's " +
                    "user management table. " +
                    "Tenant isolation is enforced at the query service layer via companyId from the JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supervisor list returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<List<SupervisorResource>> getAll() {
        var supervisors = supervisorQueryService.handle(new GetAllSupervisorsQuery()).stream()
                .map(SupervisorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(supervisors);
    }

    @PostMapping
    @Operation(
            summary = "Create a supervisor",
            description = "Registers a new supervisor account under the authenticated company. " +
                    "Internally creates a User with role SUPERVISOR and sends a welcome email " +
                    "with the generated temporary password via Brevo SMTP. " +
                    "The supervisor must change their password on first login " +
                    "(the `requiresPasswordChange` flag is set to true). " +
                    "Returns 201 with the created supervisor resource on success.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Supervisor created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or email already in use"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or ADMIN role required")
    })
    public ResponseEntity<?> create(@RequestBody CreateSupervisorResource resource) {
        var command = CreateSupervisorCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = supervisorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, SupervisorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{supervisorId}")
    @Operation(
            summary = "Update a supervisor",
            description = "Replaces the profile of an existing supervisor (name, email, phone, status). " +
                    "PUT is used because the administration form always submits all fields together. " +
                    "The supervisor must belong to the authenticated company — " +
                    "ownership is enforced at the command service layer. " +
                    "Password changes are NOT handled by this endpoint — use PATCH /api/v1/users/me/password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Supervisor updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Supervisor not found or does not belong to this tenant"),
            @ApiResponse(responseCode = "403", description = "Access denied — JWT missing or invalid")
    })
    public ResponseEntity<?> update(
            @Parameter(description = "Unique numeric identifier of the supervisor to update", required = true)
            @PathVariable("supervisorId") Long supervisorId,
            @RequestBody UpdateSupervisorResource resource) {
        var command = UpdateSupervisorCommandFromResourceAssembler.toCommandFromResource(supervisorId, resource);
        var result = supervisorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, SupervisorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
