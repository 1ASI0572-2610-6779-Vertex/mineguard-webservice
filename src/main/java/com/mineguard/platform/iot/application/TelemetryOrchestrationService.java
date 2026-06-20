package com.mineguard.platform.iot.application;

import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Facade that orchestrates all downstream actions for a single telemetry payload. */
public interface TelemetryOrchestrationService {
    Result<TelemetryIngestionResponse, ApplicationError> orchestrate(TelemetryIngestionRequest request);
}
