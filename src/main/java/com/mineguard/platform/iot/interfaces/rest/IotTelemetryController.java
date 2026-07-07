package com.mineguard.platform.iot.interfaces.rest;

import com.mineguard.platform.iot.application.TelemetryOrchestrationService;
import com.mineguard.platform.iot.infrastructure.security.EdgeApiKeyFilter;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Unified IoT telemetry ingestion endpoint consumed by edge computing devices
 * deployed in the mine. Authentication is performed at the filter level via the
 * {@code X-API-Key} header (see {@link EdgeApiKeyFilter}) — no JWT is required.
 * Renamed from {@code /api/v1/iot/telemetry} — telemetry is the resource, "iot" was
 * a transport-concern prefix, not a noun.
 */
@RestController
@RequestMapping(value = "/api/v1/telemetry", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "ApiKey")
@Tag(
        name = "IoT Telemetry (Edge — M2M)",
        description = """
                Unified telemetry ingestion contract for all edge computing devices.
                This endpoint replaces the per-sensor ingestion paths and accepts a single
                enriched payload that may contain heart-rate (BPM), GPS coordinates,
                proximity distance and collision events in one request, reducing radio
                bandwidth in the mine.

                **Authentication:** Machine-to-machine via the `X-API-Key` request header.
                The key is unique per company (generated at company registration time) —
                there is no shared platform-wide secret. JWT bearer tokens are NOT accepted
                on this route. Requests without a valid key receive `401 Unauthorized`.
                """
)
public class IotTelemetryController {

    private final TelemetryOrchestrationService orchestrationService;

    public IotTelemetryController(TelemetryOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping
    @Operation(
            summary = "Ingest unified sensor telemetry",
            description = """
                    Receives a unified telemetry payload from an edge computing device and
                    orchestrates the following downstream actions:

                    1. **Sensor resolution** — looks up the `device_id` scoped to the caller's
                       company (resolved from the X-API-Key) to obtain `vehicleId` and `tripId`.
                       Scoping by company means two tenants can never collide on the same
                       `device_id`.
                    2. **Cardiac health** — if `bpm > 0`, persists a heart-rate SensorReading.
                    3. **GPS location** — if `lat` and `lng` are present, updates the live-map
                       vehicle marker for real-time fleet tracking.
                    4. **Proximity / collision telemetry** — persists distance and collision
                       samples when present.
                    5. **Proximity / collision alert** — if `collision == true` OR
                       `distance_cm ≤ 20 cm`, raises a CRITICAL proximity alert. The alert is
                       linked to the active trip when one exists.

                    The `processed` field in the response is a JSON array of the executed action
                    names (e.g. `["cardiac", "location", "alert"]`), not a comma-joined string, so
                    the edge firmware can parse it directly.

                    Authentication is the `X-API-Key` header declared on this operation's security
                    scheme (see the "Authorize" padlock above) — requests without it, or with an
                    unrecognized value, are rejected with 401 before reaching the controller.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Telemetry ingested — response lists executed actions",
                    content = @Content(schema = @Schema(implementation = TelemetryIngestionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid X-API-Key header"),
            @ApiResponse(responseCode = "404", description = "Unknown device_id for this company — sensor not registered"),
            @ApiResponse(responseCode = "400", description = "Malformed payload")
    })
    public ResponseEntity<?> ingest(@RequestBody TelemetryIngestionRequest request, HttpServletRequest servletRequest) {
        var companyId = (Long) servletRequest.getAttribute(EdgeApiKeyFilter.COMPANY_ID_ATTRIBUTE);
        if (companyId == null) {
            return ResponseEntityAssembler.toResponseEntityFromResult(
                    Result.<TelemetryIngestionResponse, ApplicationError>failure(
                            ApplicationError.unexpected("telemetry", "Company could not be resolved from X-API-Key")),
                    r -> r, HttpStatus.CREATED);
        }
        var result = orchestrationService.orchestrate(request, companyId);
        return ResponseEntityAssembler.toResponseEntityFromResult(result, r -> r, HttpStatus.CREATED);
    }
}
