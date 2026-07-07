package com.mineguard.platform.assets.application.commandservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.commands.CreateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.DeactivateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateDriverCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface DriverCommandService {
    Result<Driver, ApplicationError> handle(CreateDriverCommand command);
    Result<Driver, ApplicationError> handle(UpdateDriverCommand command);

    /** Deactivates (soft-deletes) a driver, marking it INACTIVE. Preserves historical records. */
    Result<Driver, ApplicationError> handle(DeactivateDriverCommand command);
}
