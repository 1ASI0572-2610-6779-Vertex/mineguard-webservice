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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Web fleet inventory endpoint ({@code /vehiclesInventory}). */
@RestController
@RequestMapping(value = "/vehiclesInventory", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicles Inventory", description = "Fleet vehicle inventory administration")
public class VehiclesInventoryController {
    private final VehicleQueryService vehicleQueryService;
    private final VehicleCommandService vehicleCommandService;

    public VehiclesInventoryController(VehicleQueryService vehicleQueryService, VehicleCommandService vehicleCommandService) {
        this.vehicleQueryService = vehicleQueryService;
        this.vehicleCommandService = vehicleCommandService;
    }

    @GetMapping
    public ResponseEntity<List<VehicleResource>> getAll() {
        var vehicles = vehicleQueryService.handle(new GetAllVehiclesQuery()).stream()
                .map(VehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateVehicleResource resource) {
        var command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = vehicleCommandService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, VehicleResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.OK);
    }
}
