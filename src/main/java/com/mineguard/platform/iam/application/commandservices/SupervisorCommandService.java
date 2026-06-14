package com.mineguard.platform.iam.application.commandservices;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.commands.UpdateSupervisorCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Command service port for supervisor account management. */
public interface SupervisorCommandService {
    Result<Supervisor, ApplicationError> handle(CreateSupervisorCommand command);
    Result<Supervisor, ApplicationError> handle(UpdateSupervisorCommand command);
}
