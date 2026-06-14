package com.mineguard.platform.assets.application.internal.commandservices;

import com.mineguard.platform.assets.application.commandservices.VehicleCommandService;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.commands.CreateVehicleCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateVehicleCommand;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {
    private final VehicleRepository vehicleRepository;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Result<Vehicle, ApplicationError> handle(CreateVehicleCommand command) {
        var vehicle = new Vehicle(command.code(), command.model(), command.category(), command.status(),
                command.assignedDriverName(), command.shiftLabel());
        return Result.success(vehicleRepository.save(vehicle));
    }

    @Override
    public Result<Vehicle, ApplicationError> handle(UpdateVehicleCommand command) {
        var existing = vehicleRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.id())));
        }
        var vehicle = existing.get();
        vehicle.updateInformation(command.code(), command.model(), command.category(), command.status(),
                command.assignedDriverName(), command.shiftLabel());
        return Result.success(vehicleRepository.save(vehicle));
    }
}
