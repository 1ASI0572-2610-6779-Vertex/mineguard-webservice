package com.mineguard.platform.assets.application.internal.commandservices;

import com.mineguard.platform.assets.application.commandservices.TripCommandService;
import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.commands.CreateTripCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.domain.repositories.DriverRepository;
import com.mineguard.platform.assets.domain.repositories.TripRepository;
import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TripCommandServiceImpl implements TripCommandService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final SecurityContextFacade securityContext;

    public TripCommandServiceImpl(TripRepository tripRepository,
                                  VehicleRepository vehicleRepository,
                                  DriverRepository driverRepository,
                                  SecurityContextFacade securityContext) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.securityContext = securityContext;
    }

    @Override
    public Result<Trip, ApplicationError> handle(CreateTripCommand command) {
        var companyId = securityContext.currentCompanyId();

        var vehicleOpt = vehicleRepository.findById(command.vehicleId());
        if (vehicleOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.vehicleId())));
        }
        // Prevenir check-in en vehículos de otro tenant
        if (companyId != null && !companyId.equals(vehicleOpt.get().getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.vehicleId())));
        }

        var driverOpt = driverRepository.findById(command.driverId());
        if (driverOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Driver", String.valueOf(command.driverId())));
        }
        // Prevenir check-in con conductores de otro tenant
        if (companyId != null && !companyId.equals(driverOpt.get().getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Driver", String.valueOf(command.driverId())));
        }

        var trip = new Trip(command.driverId(), command.vehicleId(),
                Instant.now().toString(), null, TripStatus.IN_PROGRESS);
        trip.setCompanyId(companyId);
        return Result.success(tripRepository.save(trip));
    }
}
