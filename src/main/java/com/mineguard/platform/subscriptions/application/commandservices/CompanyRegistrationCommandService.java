package com.mineguard.platform.subscriptions.application.commandservices;

import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.subscriptions.domain.model.commands.RegisterCompanyCommand;

public interface CompanyRegistrationCommandService {
    Result<String, ApplicationError> handle(RegisterCompanyCommand command);
}
