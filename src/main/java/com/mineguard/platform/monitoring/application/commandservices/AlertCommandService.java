package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.CreateCardiacAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateEmergencySosAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.CreateProximityAlertCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface AlertCommandService {
    /** Partial update (PATCH semantics) — used both for classification edits and status-change actions. */
    Result<Alert, ApplicationError> handle(UpdateAlertCommand command);
    Result<Alert, ApplicationError> handle(CreateProximityAlertCommand command);

    /** Creates an alert from a heart-rate sample already classified as alert-worthy. */
    Result<Alert, ApplicationError> handle(CreateCardiacAlertCommand command);

    /** Creates the CRITICAL alert raised by a manual SOS button press. */
    Result<Alert, ApplicationError> handle(CreateEmergencySosAlertCommand command);
}
