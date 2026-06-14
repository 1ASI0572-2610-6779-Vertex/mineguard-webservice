package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.commands.MarkAlertActionCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateAlertCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface AlertCommandService {
    Result<Alert, ApplicationError> handle(UpdateAlertCommand command);
    Result<Alert, ApplicationError> handle(MarkAlertActionCommand command);
}
