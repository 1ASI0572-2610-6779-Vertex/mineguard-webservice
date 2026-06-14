package com.mineguard.platform.assets.interfaces.rest;

import com.mineguard.platform.assets.application.queryservices.DriverQueryService;
import com.mineguard.platform.assets.domain.model.queries.GetAllDriversQuery;
import com.mineguard.platform.assets.interfaces.rest.resources.DriverResource;
import com.mineguard.platform.assets.interfaces.rest.transform.DriverResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Web drivers directory endpoint ({@code /driversDirectory}). */
@RestController
@RequestMapping(value = "/driversDirectory", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Drivers Directory", description = "Driver directory")
public class DriversDirectoryController {
    private final DriverQueryService driverQueryService;

    public DriversDirectoryController(DriverQueryService driverQueryService) {
        this.driverQueryService = driverQueryService;
    }

    @GetMapping
    public ResponseEntity<List<DriverResource>> getAll() {
        var drivers = driverQueryService.handle(new GetAllDriversQuery()).stream()
                .map(DriverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(drivers);
    }
}
