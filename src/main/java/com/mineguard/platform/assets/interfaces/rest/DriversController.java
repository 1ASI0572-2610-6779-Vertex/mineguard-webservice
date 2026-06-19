package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.commandservices.DriverCommandService;
import com.mineguard.platform.assets.application.queryservices.DriverQueryService;
import com.mineguard.platform.assets.domain.model.commands.CreateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateDriverCommand;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByIdQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.CreateDriverResource;
import com.mineguard.platform.assets.interfaces.rest.resources.UpdateDriverResource;
import com.mineguard.platform.assets.interfaces.rest.transform.DriverResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/drivers", produces = MediaType.APPLICATION_JSON_VALUE)
public class DriversController {
    private final DriverCommandService driverCommandService;
    private final DriverQueryService driverQueryService;

    public DriversController(DriverCommandService driverCommandService, DriverQueryService driverQueryService) {
        this.driverCommandService = driverCommandService;
        this.driverQueryService = driverQueryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var driver = driverQueryService.handle(new GetDriverByIdQuery(id));
        if (driver.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(DriverResourceFromEntityAssembler.toResourceFromEntity(driver.get()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDriverResource resource) {
        var command = new CreateDriverCommand(resource.username(), resource.password(), resource.email(),
                resource.fullName(), resource.idCompany(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateDriverResource resource) {
        var command = new UpdateDriverCommand(id, resource.username(), resource.password(), resource.email(),
                resource.fullName(), resource.idCompany(), resource.licenseNumber(), resource.workShift());
        return ResponseEntityAssembler.toResponseEntityFromResult(driverCommandService.handle(command),
                DriverResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
