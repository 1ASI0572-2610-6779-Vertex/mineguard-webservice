package com.mineguard.platform.assets.application.commandservices;

import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.commands.CreateTripCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface TripCommandService {
    Result<Trip, ApplicationError> handle(CreateTripCommand command);
}
