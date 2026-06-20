package com.mineguard.platform.iot.application.internal;

import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.iot.application.TelemetryOrchestrationService;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.monitoring.application.commandservices.AlertCommandService;
import com.mineguard.platform.monitoring.application.commandservices.CardiacReadingCommandService;
import com.mineguard.platform.monitoring.application.commandservices.LiveMapVehicleCommandService;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.IngestCardiacReadingCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateVehicleLocationCommand;
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
 * <p>Step 4 — Raise CRITICAL proximity alert when collision==true OR distance_cm ≤ 40.</p>
 */
@Service
public class TelemetryOrchestrationServiceImpl implements TelemetryOrchestrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryOrchestrationServiceImpl.class);
    private static final int PROXIMITY_THRESHOLD_CM = 40;

    private final SensorRepository sensorRepository;
    private final TripRepository tripRepository;
    private final CardiacReadingCommandService cardiacReadingCommandService;
    private final LiveMapVehicleCommandService liveMapVehicleCommandService;
    private final AlertCommandService alertCommandService;

    public TelemetryOrchestrationServiceImpl(
            SensorRepository sensorRepository,
            TripRepository tripRepository,
            CardiacReadingCommandService cardiacReadingCommandService,
            LiveMapVehicleCommandService liveMapVehicleCommandService,
            AlertCommandService alertCommandService) {
        this.sensorRepository = sensorRepository;
        this.tripRepository = tripRepository;
        this.cardiacReadingCommandService = cardiacReadingCommandService;
        this.liveMapVehicleCommandService = liveMapVehicleCommandService;
        this.alertCommandService = alertCommandService;
    }

    @Override
    public Result<TelemetryIngestionResponse, ApplicationError> orchestrate(TelemetryIngestionRequest request) {

        // ── Step 1: Resolve sensor context ──────────────────────────────────────────
        var sensorOpt = sensorRepository.findByDeviceId(request.deviceId());
        if (sensorOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Sensor", request.deviceId()));
        }
        var sensor = sensorOpt.get();
        var vehicleId = sensor.getVehicleId();

        var activeTrip = tripRepository.findFirstByVehicleIdAndStatus(vehicleId, TripStatus.IN_PROGRESS);
        Long tripId    = activeTrip.map(t -> t.getId()).orElse(null);
        Long companyId = activeTrip.map(t -> t.getCompanyId()).orElse(null);

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

        // ── Step 4: Proximity / collision alert ──────────────────────────────────────
        boolean proximityViolation = request.distanceCm() != null && request.distanceCm() <= PROXIMITY_THRESHOLD_CM;
        if (request.collision() || proximityViolation) {
            if (tripId != null) {
                var alertCmd = new CreateProximityAlertCommand(
                        tripId, sensor.getId(), companyId,
                        request.distanceCm(), request.collision(), occurredAt);
                var alertResult = alertCommandService.handle(alertCmd);
                if (alertResult.isSuccess()) {
                    processed.add("alert");
                    alertRaised = true;
                } else {
                    LOGGER.warn("Alert creation failed for trip {}: {}", tripId,
                            ((Result.Failure<?, ApplicationError>) alertResult).error().message());
                }
            } else {
                LOGGER.warn("Proximity event for device {} but no active trip found for vehicle {} — alert skipped",
                        request.deviceId(), vehicleId);
            }
        }

        var response = new TelemetryIngestionResponse(
                request.deviceId(),
                String.join(",", processed),
                alertRaised,
                "Telemetry ingested: " + processed.size() + " action(s) executed"
        );
        return Result.success(response);
    }
}
