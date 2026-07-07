package com.mineguard.platform.assets.application.commandservices;

import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.commands.ArchiveVehicleCommand;
import com.mineguard.platform.assets.domain.model.commands.CreateVehicleCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateVehicleCommand;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;

public interface VehicleCommandService {
    Result<Vehicle, ApplicationError> handle(CreateVehicleCommand command);
    Result<Vehicle, ApplicationError> handle(UpdateVehicleCommand command);

    /** Archives (soft-deletes) a vehicle. Fails with 409 while it still has an active device. */
    Result<Vehicle, ApplicationError> handle(ArchiveVehicleCommand command);
}
