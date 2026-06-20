package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.SensorReading;
import com.mineguard.platform.monitoring.domain.model.commands.IngestCardiacReadingCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Persists a heart-rate sensor reading arriving from the unified IoT telemetry pipeline. */
public interface CardiacReadingCommandService {
    Result<SensorReading, ApplicationError> handle(IngestCardiacReadingCommand command);
}
