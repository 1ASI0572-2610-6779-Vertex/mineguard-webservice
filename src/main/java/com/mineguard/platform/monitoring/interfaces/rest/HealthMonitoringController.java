package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.HeartRateIngestionService;
import com.mineguard.platform.monitoring.domain.model.commands.IngestHeartRateCommand;
import com.mineguard.platform.monitoring.interfaces.rest.resources.HealthDataRecordResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.HealthRecordResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Smart-band edge ingestion endpoint. Cloud counterpart of the
 * {@code smart-band-edge-service} data-records endpoint, authenticated by
 * device id + {@code X-API-Key}.
 */
@RestController
@RequestMapping(value = "/api/v1/health-monitoring", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Health Monitoring (Edge)", description = "Smart-band IoT heart-rate ingestion")
public class HealthMonitoringController {
    private final HeartRateIngestionService heartRateIngestionService;

    public HealthMonitoringController(HeartRateIngestionService heartRateIngestionService) {
        this.heartRateIngestionService = heartRateIngestionService;
    }

    @PostMapping("/data-records")
    @Operation(summary = "Ingest heart-rate record",
            description = "Creates a heart-rate record for an authenticated smart-band device (X-API-Key header).")
    public ResponseEntity<?> create(@RequestHeader(value = "X-API-Key", required = false) String apiKey,
                                    @RequestBody HealthDataRecordResource resource) {
        var command = new IngestHeartRateCommand(resource.deviceId(), resource.bpm(), resource.createdAt(), apiKey);
        var result = heartRateIngestionService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, HealthRecordResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
