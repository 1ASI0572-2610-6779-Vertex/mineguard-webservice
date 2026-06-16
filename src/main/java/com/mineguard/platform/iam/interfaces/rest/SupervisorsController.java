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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/supervisors", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Supervisors", description = "Supervisor account administration")
public class SupervisorsController {
    private final SupervisorCommandService supervisorCommandService;
    private final SupervisorQueryService supervisorQueryService;

    public SupervisorsController(SupervisorCommandService supervisorCommandService,
                                 SupervisorQueryService supervisorQueryService) {
        this.supervisorCommandService = supervisorCommandService;
        this.supervisorQueryService = supervisorQueryService;
    }

    @GetMapping
    @Operation(summary = "List supervisors", description = "Returns a flat array of supervisors.")
    public ResponseEntity<List<SupervisorResource>> getAll() {
        var supervisors = supervisorQueryService.handle(new GetAllSupervisorsQuery()).stream()
                .map(SupervisorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(supervisors);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Operation(summary = "Create supervisor (Only for Administrators)")
    public ResponseEntity<?> create(@RequestBody CreateSupervisorResource resource) {
        var command = CreateSupervisorCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = supervisorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, SupervisorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Operation(summary = "Update supervisor (Only for Administrators)")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateSupervisorResource resource) {
        var command = UpdateSupervisorCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = supervisorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, SupervisorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}