package com.mineguard.platform.iam.application.commandservices;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.domain.model.commands.CreateAdministratorCommand;
import com.mineguard.platform.iam.domain.model.commands.UpdateAdministratorCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface AdministratorCommandService {
    Result<Administrator, ApplicationError> handle(CreateAdministratorCommand command);
    Result<Administrator, ApplicationError> handle(UpdateAdministratorCommand command);
}