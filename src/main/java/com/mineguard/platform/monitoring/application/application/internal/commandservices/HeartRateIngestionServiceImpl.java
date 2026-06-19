package com.mineguard.platform.monitoring.application.application.internal.commandservices;

import com.mineguard.platform.iam.interfaces.acl.IamContextFacade;
import com.mineguard.platform.monitoring.application.commandservices.HeartRateIngestionService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;
import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.domain.model.commands.IngestHeartRateCommand;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertPriority;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertStatus;
import com.mineguard.platform.monitoring.domain.model.valueobjects.AlertType;
import com.mineguard.platform.monitoring.domain.model.valueobjects.CardiacStatus;
import com.mineguard.platform.monitoring.domain.repositories.AlertRepository;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Cloud-side ingestion of smart-band heart-rate telemetry. Authenticates the
 * device against IAM (X-API-Key), persists the reading and raises a fatigue
 * alert when the derived cardiac status is abnormal (US007).
 */
@Service
public class HeartRateIngestionServiceImpl implements HeartRateIngestionService {
    private final SensorReadingRepository sensorReadingRepository;
    private final AlertRepository alertRepository;
    private final IamContextFacade iamContextFacade;

    public HeartRateIngestionServiceImpl(SensorReadingRepository sensorReadingRepository,
                                         AlertRepository alertRepository,
                                         IamContextFacade iamContextFacade) {
        this.sensorReadingRepository = sensorReadingRepository;
        this.alertRepository = alertRepository;
        this.iamContextFacade = iamContextFacade;
    }

    @Override
    public Result<HeartRateReading, ApplicationError> handle(IngestHeartRateCommand command) {
        if (command.deviceId() == null || command.apiKey() == null) {
            return Result.failure(ApplicationError.validationError("device", "Missing device_id or X-API-Key"));
        }
        if (!iamContextFacade.authenticateDevice(command.deviceId(), command.apiKey())) {
            return Result.failure(ApplicationError.validationError("device", "Invalid device_id or API key"));
        }
        if (command.bpm() <= 0) {
            return Result.failure(ApplicationError.validationError("bpm", "Invalid BPM value"));
        }
        var driverId = iamContextFacade.getDriverIdByDeviceId(command.deviceId()).orElse(null);
        var createdAt = command.createdAt() != null ? command.createdAt() : java.time.Instant.now().toString();
        var reading = new HeartRateReading(command.deviceId(), driverId, command.bpm(), createdAt);
        var savedTelemetry = sensorReadingRepository.save(new SensorReading(driverId, "heart_rate", command.bpm(), createdAt));
        reading.setId(savedTelemetry.getId());

        if (reading.indicatesFatigue()) {
            var status = reading.cardiacStatus();
            var priority = status == CardiacStatus.CRITICAL ? AlertPriority.CRITICAL : AlertPriority.WARNING;
            var alert = new Alert(
                    "HR-" + reading.getId(), AlertType.FATIGUE, priority, AlertStatus.ACTIVE, createdAt,
                    "Fatigue detected", "Abnormal heart rate (" + command.bpm() + " bpm) reported by device " + command.deviceId(),
                    "monitoring.alerts.vehicleClass.light", null, null, null);
            alert.setRawType("high_heart_rate");
            alert.setSeverity(priority.toSerialized());
            alertRepository.save(alert);
        }
        return Result.success(reading);
    }
}
