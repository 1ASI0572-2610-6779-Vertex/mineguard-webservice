package com.mineguard.platform.iot.interfaces.rest;

import com.mineguard.platform.iot.application.TelemetryOrchestrationService;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Unified IoT telemetry ingestion endpoint consumed by edge computing devices
 * deployed in the mine. Authentication is performed at the filter level via the
 * {@code X-API-Key} header (see {@code EdgeApiKeyFilter}) — no JWT is required.
 */
@RestController
@RequestMapping(value = "/api/v1/iot", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(
        name = "IoT Telemetry (Edge — M2M)",
        description = """
                Unified telemetry ingestion contract for all edge computing devices.
                This endpoint replaces the per-sensor ingestion paths and accepts a single
                enriched payload that may contain heart-rate (BPM), GPS coordinates,
                proximity distance and collision events in one request, reducing radio
                bandwidth in the mine.

                **Authentication:** Machine-to-machine via the `X-API-Key` request header.
                The key must match the value configured in `${EDGE_DEFAULT_API_KEY}`.
                JWT bearer tokens are NOT accepted on `/api/v1/iot/**` routes.
                Requests without a valid key receive `401 Unauthorized`.
                """
)
public class IotTelemetryController {

    private final TelemetryOrchestrationService orchestrationService;

    public IotTelemetryController(TelemetryOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/telemetry")
    @Operation(
            summary = "Ingest unified sensor telemetry",
            description = """
                    Receives a unified telemetry payload from an edge computing device and
                    orchestrates the following downstream actions:

                    1. **Sensor resolution** — looks up the `device_id` to obtain `vehicleId`,
                       `tripId` and `companyId` (multi-tenant isolation is preserved).
                    2. **Cardiac health** — if `bpm > 0`, persists a heart-rate SensorReading.
                    3. **GPS location** — if `lat` and `lng` are present, updates the live-map
                       vehicle marker for real-time fleet tracking.
                    4. **Proximity / collision alert** — if `collision == true` OR
                       `distance_cm ≤ 40 cm`, raises a CRITICAL proximity alert linked to
                       the active trip (multi-tenant safe via `companyId`).

                    The `processed` field in the response lists which actions were executed
                    (`cardiac`, `location`, `alert`) so the edge device can log them locally.
                    """,
            parameters = @Parameter(
                    name = "X-API-Key",
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Pre-shared machine-to-machine API key. " +
                            "Configured server-side via `${EDGE_DEFAULT_API_KEY}`. " +
                            "Requests without this header or with an incorrect value " +
                            "are rejected with 401 before reaching the controller.",
                    schema = @Schema(type = "string", example = "test-api-key-123")
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Telemetry ingested — response lists executed actions",
                    content = @Content(schema = @Schema(implementation = TelemetryIngestionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-Key header"),
            @ApiResponse(responseCode = "404", description = "Unknown device_id — sensor not registered"),
            @ApiResponse(responseCode = "400", description = "Malformed payload")
    })
    public ResponseEntity<?> ingest(@RequestBody TelemetryIngestionRequest request) {
        var result = orchestrationService.orchestrate(request);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, r -> r, HttpStatus.CREATED);
    }
}
