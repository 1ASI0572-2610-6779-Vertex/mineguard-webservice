package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.monitoring.application.commandservices.CardiacReadingCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.domain.model.commands.IngestCardiacReadingCommand;
import com.mineguard.platform.monitoring.domain.repositories.SensorReadingRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/** Persists a heart-rate SensorReading from the unified IoT telemetry pipeline. */
@Service
public class CardiacReadingCommandServiceImpl implements CardiacReadingCommandService {

    private final SensorReadingRepository sensorReadingRepository;

    public CardiacReadingCommandServiceImpl(SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
    }

    @Override
    public Result<SensorReading, ApplicationError> handle(IngestCardiacReadingCommand command) {
        if (command.bpm() <= 0) {
            return Result.failure(ApplicationError.validationError("bpm", "BPM must be greater than zero"));
        }
        var reading = new SensorReading(command.sensorId(), "heart_rate", command.bpm(), command.occurredAt());
        return Result.success(sensorReadingRepository.save(reading));
    }
}
