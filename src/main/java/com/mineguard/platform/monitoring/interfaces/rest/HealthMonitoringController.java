package com.mineguard.platform.monitoring.interfaces.rest;

import com.mineguard.platform.monitoring.application.commandservices.HeartRateIngestionService;
import com.mineguard.platform.monitoring.domain.model.commands.IngestHeartRateCommand;
import com.mineguard.platform.monitoring.interfaces.rest.resources.HealthDataRecordResource;
import com.mineguard.platform.monitoring.interfaces.rest.transform.HealthRecordResourceFromEntityAssembler;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @deprecated Replaced by the unified IoT ingestion contract.
 *             Use {@code POST /api/v1/iot/telemetry} instead.
 *             This endpoint will be removed in the next major release.
 */
@Deprecated(since = "2.0", forRemoval = true)
@RestController
@RequestMapping(value = "/api/v1/health-monitoring", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Health Monitoring (Edge) [DEPRECATED]",
        description = "**DEPRECATED** — Use `POST /api/v1/iot/telemetry` instead. " +
                "Smart-band IoT ingestion endpoint — cloud counterpart of the " +
                "smart-band-edge-service. This path is intentionally kept separate from the user-facing API hierarchy: " +
                "it is called by IoT devices (smart-bands) authenticated with an X-API-Key header, not by JWT users. " +
                "Ingested SensorReadings of type `heart_rate` become the raw data source for " +
                "GET /api/v1/trips/{tripId}/cardiac-readings.")
public class HealthMonitoringController {

    private final HeartRateIngestionService heartRateIngestionService;

    public HealthMonitoringController(HeartRateIngestionService heartRateIngestionService) {
        this.heartRateIngestionService = heartRateIngestionService;
    }

    @PostMapping("/data-records")
    @Operation(
            summary = "Ingest a heart-rate record",
            description = "Receives a heart-rate reading from a smart-band device and persists it as a SensorReading " +
                    "of type `heart_rate`. Authentication is device-level via the X-API-Key header (not JWT). " +
                    "The `deviceId` in the body is mapped to a Sensor record to determine which Vehicle and Driver " +
                    "the reading belongs to. " +
                    "If the BPM crosses the `critical` threshold (≥ 140), the ingestion service may automatically " +
                    "create a `high_heart_rate` Alert linked to the active Trip of that Vehicle.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Heart-rate record ingested successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payload or unknown deviceId"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-Key header")
    })
    public ResponseEntity<?> create(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey,
            @RequestBody HealthDataRecordResource resource) {
        var command = new IngestHeartRateCommand(resource.deviceId(), resource.bpm(), resource.createdAt(), apiKey);
        var result = heartRateIngestionService.handle(command);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result, HealthRecordResourceFromEntityAssembler::toResourceFromEntity, HttpStatus.CREATED);
    }
}
