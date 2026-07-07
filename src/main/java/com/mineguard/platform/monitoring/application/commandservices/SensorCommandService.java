package com.mineguard.platform.monitoring.application.commandservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.model.commands.CreateSensorCommand;
import com.mineguard.platform.monitoring.domain.model.commands.LinkDeviceCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateSensorCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface SensorCommandService {
    Result<Sensor, ApplicationError> handle(CreateSensorCommand command);

    /** Links a new MineGuard device to a vehicle, assigning the next per-company sequential deviceId. */
    Result<Sensor, ApplicationError> handle(LinkDeviceCommand command);

    /** Partially updates a device: reassign to another vehicle and/or change its lifecycle status. */
    Result<Sensor, ApplicationError> handle(UpdateSensorCommand command);
}