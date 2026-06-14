package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;
import com.mineguard.platform.monitoring.domain.model.commands.IngestHeartRateCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Ingests heart-rate telemetry synchronized from smart-band edge devices. */
public interface HeartRateIngestionService {
    Result<HeartRateReading, ApplicationError> handle(IngestHeartRateCommand command);
}
