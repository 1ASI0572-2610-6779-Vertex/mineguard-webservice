package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.queryservices.LiveMapVehicleQueryService;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllLiveMapVehiclesQuery;
import com.mineguard.platform.monitoring.interfaces.rest.resources.LiveMapVehicleResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.LiveMapVehicleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Web live-map vehicles endpoint ({@code /liveMapVehicles}). */
@RestController
@RequestMapping(value = "/liveMapVehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Live Map Vehicles", description = "Real-time vehicle positions")
public class LiveMapVehiclesController {
    private final LiveMapVehicleQueryService liveMapVehicleQueryService;

    public LiveMapVehiclesController(LiveMapVehicleQueryService liveMapVehicleQueryService) {
        this.liveMapVehicleQueryService = liveMapVehicleQueryService;
    }

    @GetMapping
    public ResponseEntity<List<LiveMapVehicleResource>> getAll() {
        var vehicles = liveMapVehicleQueryService.handle(new GetAllLiveMapVehiclesQuery()).stream()
                .map(LiveMapVehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicles);
    }
}
