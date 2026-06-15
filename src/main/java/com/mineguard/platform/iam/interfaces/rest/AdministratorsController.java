package com.mineguard.platform.iam.interfaces.rest;

import com.mineguard.platform.iam.application.commandservices.AdministratorCommandService;
import com.mineguard.platform.iam.application.queryservices.AdministratorQueryService;
import com.mineguard.platform.iam.domain.model.queries.GetAllAdministratorsQuery;
import com.mineguard.platform.iam.interfaces.rest.resources.AdministratorResource;
import com.mineguard.platform.iam.interfaces.rest.resources.CreateAdministratorResource;
import com.mineguard.platform.iam.interfaces.rest.resources.UpdateAdministratorResource;
import com.mineguard.platform.iam.interfaces.rest.transform.AdministratorResourceFromEntityAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.CreateAdministratorCommandFromResourceAssembler;
import com.mineguard.platform.iam.interfaces.rest.transform.UpdateAdministratorCommandFromResourceAssembler;
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
@RequestMapping(value = "/administrators", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Administrators", description = "Administrator account management")
public class AdministratorsController {
    private final AdministratorCommandService administratorCommandService;
    private final AdministratorQueryService administratorQueryService;

    public AdministratorsController(AdministratorCommandService administratorCommandService,
                                    AdministratorQueryService administratorQueryService) {
        this.administratorCommandService = administratorCommandService;
        this.administratorQueryService = administratorQueryService;
    }

    @GetMapping
    @Operation(summary = "Get all administrators")
    public ResponseEntity<List<AdministratorResource>> getAll() {
        var admins = administratorQueryService.handle(new GetAllAdministratorsQuery()).stream()
                .map(AdministratorResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(admins);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Operation(summary = "Create an administrator (Only for Administrators)")
    public ResponseEntity<?> create(@RequestBody CreateAdministratorResource resource) {
        var command = CreateAdministratorCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = administratorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AdministratorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Operation(summary = "Update an administrator (Only for Administrators)")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateAdministratorResource resource) {
        var command = UpdateAdministratorCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = administratorCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, AdministratorResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}