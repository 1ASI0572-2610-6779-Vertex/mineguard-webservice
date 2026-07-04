package com.mineguard.platform.iot.application;

import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionRequest;
import com.mineguard.platform.iot.interfaces.rest.resources.TelemetryIngestionResponse;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Facade that orchestrates all downstream actions for a single telemetry payload. */
public interface TelemetryOrchestrationService {
    /**
     * @param companyId tenant resolved from the caller's X-API-Key (see EdgeApiKeyFilter) —
     *                   the device_id is looked up scoped to this company, so two tenants
     *                   provisioning a sensor with the same device_id can never collide.
     */
    Result<TelemetryIngestionResponse, ApplicationError> orchestrate(TelemetryIngestionRequest request, Long companyId);
}
