package com.mineguard.platform.assets.application.commandservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.commands.CreateDriverCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateDriverCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface DriverCommandService {
    Result<Driver, ApplicationError> handle(CreateDriverCommand command);
    Result<Driver, ApplicationError> handle(UpdateDriverCommand command);
}
