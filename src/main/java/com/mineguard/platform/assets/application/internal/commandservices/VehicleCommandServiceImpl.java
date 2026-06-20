package com.mineguard.platform.assets.application.internal.commandservices;

import com.mineguard.platform.assets.application.commandservices.VehicleCommandService;
import com.mineguard.platform.assets.domain.model.aggregates.Vehicle;
import com.mineguard.platform.assets.domain.model.commands.CreateVehicleCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateVehicleCommand;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.internal.AuditLogWriter;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {
    private final VehicleRepository vehicleRepository;
    private final AuditLogWriter auditLogWriter;
    private final SecurityContextFacade securityContext;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository, AuditLogWriter auditLogWriter,
                                     SecurityContextFacade securityContext) {
        this.vehicleRepository = vehicleRepository;
        this.auditLogWriter = auditLogWriter;
        this.securityContext = securityContext;
    }

    @Override
    public Result<Vehicle, ApplicationError> handle(CreateVehicleCommand command) {
        var vehicle = new Vehicle(command.code(), command.model(), command.category(), command.status(),
                command.assignedDriverName(), command.shiftLabel());
        vehicle.setCompanyId(securityContext.currentCompanyId());
        return Result.success(vehicleRepository.save(vehicle));
    }

    @Override
    public Result<Vehicle, ApplicationError> handle(UpdateVehicleCommand command) {
        var existing = vehicleRepository.findById(command.id());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.id())));
        }
        var vehicle = existing.get();
        var previousStatus = vehicle.getStatus();
        vehicle.updateInformation(command.code(), command.model(), command.category(), command.status(),
                command.assignedDriverName(), command.shiftLabel());
        var saved = vehicleRepository.save(vehicle);
        if (command.status() != null && previousStatus != command.status()) {
            auditLogWriter.record("administrative", "monitoring.audit.entries.vehicleStatusChanged.title",
                    "monitoring.audit.entries.vehicleStatusChanged.description",
                    "{\"vehicleId\":" + saved.getId() + ",\"from\":\"" + previousStatus.toSerialized() +
                            "\",\"to\":\"" + command.status().toSerialized() + "\"}",
                    "monitoring.audit.actors.adminGlobal");
        }
        return Result.success(saved);
    }
}
