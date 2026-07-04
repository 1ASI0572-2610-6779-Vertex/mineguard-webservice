package com.mineguard.platform.subscriptions.application.commandservices;

import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.subscriptions.domain.model.commands.RegisterCompanyCommand;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.CompanyRegistrationResponse;

public interface CompanyRegistrationCommandService {
    Result<CompanyRegistrationResponse, ApplicationError> handle(RegisterCompanyCommand command);
}
