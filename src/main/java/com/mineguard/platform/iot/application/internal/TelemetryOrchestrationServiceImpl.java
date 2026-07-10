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
import com.mineguard.platform.monitoring.domain.model.commands.CreateCardiacAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateEmergencySosAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.IngestCardiacReadingCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateVehicleLocationCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.CardiacCondition;
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
 * <p>Step 2 — Persist cardiac reading when bpm > 0, and raise an alert when the reading
 * breaches a {@link CardiacCondition} threshold.</p>
 * <p>Step 3 — Update live-map GPS marker when lat/lng are present.</p>
 * <p>Step 4 — Persist proximity/collision samples.</p>
 * <p>Step 5 — Raise a proximity/collision alert when collision==true OR distance_cm ≤ 20.
 * Severity is graded, not uniformly CRITICAL — see AlertCommandServiceImpl.</p>
 * <p>Step 6 — Raise a CRITICAL alert when the operator pressed the SOS button.</p>
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

        // No active Driving Session means nobody checked in: the vehicle is parked and the device is
        // powered but unmanned. Samples are still recorded — they are the vehicle's history — but an
        // alert names a driver in danger, and there is no driver. Raising one would page a supervisor
        // about an empty cab.
        boolean vehicleIsManned = tripId != null;

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

            // A stored reading is not an alert. Tachycardia, bradycardia and sustained strain each
            // raise their own Alert so the supervisor sees them next to proximity events.
            var condition = CardiacCondition.classify(request.bpm());
            if (vehicleIsManned && condition.isPresent()) {
                var cardiacAlertCmd = new CreateCardiacAlertCommand(
                        tripId, sensor.getId(), companyId, request.bpm(), condition.get(), occurredAt);
                var cardiacAlertResult = alertCommandService.handle(cardiacAlertCmd);
                if (cardiacAlertResult.isSuccess()) {
                    alertRaised = true;
                } else {
                    LOGGER.warn("Cardiac alert creation failed for device {}: {}", request.deviceId(),
                            ((Result.Failure<?, ApplicationError>) cardiacAlertResult).error().message());
                }
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
        if (vehicleIsManned && (request.collision() || proximityViolation)) {
            var alertCmd = new CreateProximityAlertCommand(
                    tripId, sensor.getId(), companyId,
                    request.distanceCm(), request.collision(), occurredAt);
            var alertResult = alertCommandService.handle(alertCmd);
            if (alertResult.isSuccess()) {
                alertRaised = true;
            } else {
                LOGGER.warn("Alert creation failed for device {} and trip {}: {}", request.deviceId(), tripId,
                        ((Result.Failure<?, ApplicationError>) alertResult).error().message());
            }
        }

        // ── Step 6: Manual emergency (SOS button) ────────────────────────────────────
        // Unconditionally CRITICAL, and deliberately NOT gated on an active Driving Session: the
        // press itself proves a human is at the vehicle, whether or not they checked in. Every other
        // alert here is inferred from a sensor threshold; this one is a person asking for help.
        if (request.sos()) {
            var sosCmd = new CreateEmergencySosAlertCommand(tripId, sensor.getId(), companyId, occurredAt);
            var sosResult = alertCommandService.handle(sosCmd);
            if (sosResult.isSuccess()) {
                processed.add("sos");
                alertRaised = true;
            } else {
                LOGGER.warn("SOS alert creation failed for device {}: {}", request.deviceId(),
                        ((Result.Failure<?, ApplicationError>) sosResult).error().message());
            }
        }

        // "alert" is reported once no matter how many alerts a single payload raised, so the
        // firmware can keep treating `processed` as a set of action names.
        if (alertRaised) {
            processed.add("alert");
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
