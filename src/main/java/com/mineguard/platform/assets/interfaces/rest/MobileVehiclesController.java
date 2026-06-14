package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.queryservices.VehicleQueryService;
import com.mineguard.platform.assets.domain.model.queries.GetAllVehiclesQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.MobileVehicleResource;
import com.mineguard.platform.assets.interfaces.rest.transform.MobileVehicleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Mobile vehicle-selection endpoint ({@code /vehicles}). */
@RestController
@RequestMapping(value = "/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Mobile Vehicles", description = "Operator vehicle selection (mobile)")
public class MobileVehiclesController {
    private final VehicleQueryService vehicleQueryService;

    public MobileVehiclesController(VehicleQueryService vehicleQueryService) {
        this.vehicleQueryService = vehicleQueryService;
    }

    @GetMapping
    public ResponseEntity<List<MobileVehicleResource>> getAll() {
        var vehicles = vehicleQueryService.handle(new GetAllVehiclesQuery()).stream()
                .map(MobileVehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicles);
    }
}
