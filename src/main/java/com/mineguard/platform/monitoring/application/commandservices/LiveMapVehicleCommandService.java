package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateVehicleLocationCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

/** Updates live-map GPS coordinates for vehicle markers pushed from edge sensors. */
public interface LiveMapVehicleCommandService {
    Result<LiveMapVehicle, ApplicationError> handle(UpdateVehicleLocationCommand command);
}
