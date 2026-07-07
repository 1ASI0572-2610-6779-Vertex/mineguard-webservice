package com.mineguard.platform.iot.application.internal;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.iot.application.TelemetryOrchestrationService;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.commandservices.CardiacReadingCommandService;
import com.mineguard.platform.monitoring.application.commandservices.LiveMapVehicleCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.IngestCardiacReadingCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateVehicleLocationCommand;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Facade that orchestrates all downstream domain actions for a single unified
 * telemetry payload received from an edge sensor.
 *
 * <p>Step 1 — Resolve sensor context (vehicleId, tripId, companyId) from device_id.</p>
 * <p>Step 2 — Persist cardiac reading when bpm > 0.</p>
 * <p>Step 3 — Update live-map GPS marker when lat/lng are present.</p>
 * <p>Step 4 — Persist proximity/collision samples and raise a CRITICAL alert when
 * collision==true OR distance_cm ≤ 20.</p>
 */
@Service
public class TelemetryOrchestrationServiceImpl implements TelemetryOrchestrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryOrchestrationServiceImpl.class);
    private static final int PROXIMITY_THRESHOLD_CM = 20;

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final TripRepository tripRepository;
    private final CardiacReadingCommandService cardiacReadingCommandService;
    private final LiveMapVehicleCommandService liveMapVehicleCommandService;
    private final AlertCommandService alertCommandService;

    public TelemetryOrchestrationServiceImpl(
            SensorRepository sensorRepository,
            SensorReadingRepository sensorReadingRepository,
            TripRepository tripRepository,
            CardiacReadingCommandService cardiacReadingCommandService,
            LiveMapVehicleCommandService liveMapVehicleCommandService,
            AlertCommandService alertCommandService) {
        this.sensorRepository = sensorRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.tripRepository = tripRepository;
        this.cardiacReadingCommandService = cardiacReadingCommandService;
        this.liveMapVehicleCommandService = liveMapVehicleCommandService;
        this.alertCommandService = alertCommandService;
    }

    @Override
    public Result<TelemetryIngestionResponse, ApplicationError> orchestrate(TelemetryIngestionRequest request, Long companyId) {

        // ── Step 1: Resolve sensor context, scoped to the caller's tenant ────────────
        // Scoping by companyId (resolved from the X-API-Key by EdgeApiKeyFilter) means two
        // companies provisioning a sensor with the same device_id can never cross-contaminate.
        var sensorOpt = sensorRepository.findByDeviceIdAndCompanyId(request.deviceId(), companyId);
        if (sensorOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Sensor", request.deviceId()));
        }
        var sensor = sensorOpt.get();
        var vehicleId = sensor.getVehicleId();

        var activeTrip = tripRepository.findFirstByVehicleIdAndStatus(vehicleId, TripStatus.IN_PROGRESS);
        Long tripId = activeTrip.map(t -> t.getId()).orElse(null);

        var occurredAt = request.timestamp() != null ? request.timestamp() : Instant.now().toString();
        List<String> processed = new ArrayList<>();
        boolean alertRaised = false;

        // ── Step 2: Cardiac health ───────────────────────────────────────────────────
        if (request.bpm() > 0) {
            var cardiacCmd = new IngestCardiacReadingCommand(sensor.getId(), vehicleId, request.bpm(), occurredAt);
            var cardiacResult = cardiacReadingCommandService.handle(cardiacCmd);
            if (cardiacResult.isSuccess()) {
                processed.add("cardiac");
            } else {
                LOGGER.warn("Cardiac ingestion failed for device {}: {}", request.deviceId(),
                        ((Result.Failure<?, ApplicationError>) cardiacResult).error().message());
            }
        }

        // ── Step 3: GPS location ─────────────────────────────────────────────────────
        if (request.lat() != null && request.lng() != null) {
            var locationCmd = new UpdateVehicleLocationCommand(vehicleId, request.lat(), request.lng());
            var locationResult = liveMapVehicleCommandService.handle(locationCmd);
            if (locationResult.isSuccess()) {
                processed.add("location");
            } else {
                LOGGER.warn("Location update failed for vehicle {}: {}", vehicleId,
                        ((Result.Failure<?, ApplicationError>) locationResult).error().message());
            }
        }

        // ── Step 4: Proximity / collision telemetry ──────────────────────────────────
        if (request.distanceCm() != null) {
            sensorReadingRepository.save(new SensorReading(
                    sensor.getId(), "distance_cm", request.distanceCm(), occurredAt));
            processed.add("proximity");
        }

        if (request.collision()) {
            sensorReadingRepository.save(new SensorReading(
                    sensor.getId(), "collision", 1, occurredAt));
            processed.add("collision");
        }

        // ── Step 5: Proximity / collision alert ──────────────────────────────────────
        boolean proximityViolation = request.distanceCm() != null && request.distanceCm() <= PROXIMITY_THRESHOLD_CM;
        if (request.collision() || proximityViolation) {
            var alertCmd = new CreateProximityAlertCommand(
                    tripId, sensor.getId(), companyId,
                    request.distanceCm(), request.collision(), occurredAt);
            var alertResult = alertCommandService.handle(alertCmd);
            if (alertResult.isSuccess()) {
                processed.add("alert");
                alertRaised = true;
            } else {
                LOGGER.warn("Alert creation failed for device {} and trip {}: {}", request.deviceId(), tripId,
                        ((Result.Failure<?, ApplicationError>) alertResult).error().message());
            }
        }

        var response = new TelemetryIngestionResponse(
                request.deviceId(),
                processed,
                alertRaised,
                "Telemetry ingested: " + processed.size() + " action(s) executed"
        );
        return Result.success(response);
    }
}
