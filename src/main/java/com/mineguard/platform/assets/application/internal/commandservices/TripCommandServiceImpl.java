package com.mineguard.platform.assets.application.internal.commandservices;

import com.mineguard.platform.assets.application.commandservices.TripCommandService;
import com.mineguard.platform.assets.domain.model.aggregates.Trip;
import com.mineguard.platform.assets.domain.model.commands.CreateTripCommand;
import com.mineguard.platform.assets.domain.model.commands.UpdateTripCommand;
import com.mineguard.platform.assets.domain.model.valueobjects.TripStatus;
import com.mineguard.platform.assets.domain.model.valueobjects.VehicleStatus;
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

        // Vehicle state machine: only an OPERATIONAL vehicle can start a driving session —
        // a vehicle under MAINTENANCE (or any non-operational status) is a state conflict (409).
        if (vehicleOpt.get().getStatus() != VehicleStatus.OPERATIONAL) {
            return Result.failure(ApplicationError.conflict("Vehicle", "Vehicle is not available for operation"));
        }

        // A driver can only be checked into one vehicle at a time — this is a state conflict
        // (409), not a validation error (400): the request is well-formed, it just can't be
        // satisfied given the driver's current state.
        if (tripRepository.findFirstByDriverIdAndStatus(command.driverId(), TripStatus.IN_PROGRESS).isPresent()) {
            return Result.failure(ApplicationError.conflict("Driver", "Driver already has an active driving session"));
        }

        // A vehicle can only have one active driving session at a time — this is a state conflict
        // (409), not a validation error (400): the request is well-formed, it just can't be
        // satisfied given the vehicle's current state.
        if (tripRepository.findFirstByVehicleIdAndStatus(command.vehicleId(), TripStatus.IN_PROGRESS).isPresent()) {
            return Result.failure(ApplicationError.conflict("Vehicle",
                    "Vehicle " + command.vehicleId() + " already has an active driving session"));
        }

        var trip = new Trip(command.driverId(), command.vehicleId(),
                Instant.now().toString(), null, TripStatus.IN_PROGRESS);
        trip.setCompanyId(companyId);
        return Result.success(tripRepository.save(trip));
    }

    @Override
    public Result<Trip, ApplicationError> handle(UpdateTripCommand command) {
        var tripOpt = tripRepository.findById(command.id());
        if (tripOpt.isEmpty()) {
            return Result.failure(ApplicationError.notFound("DrivingSession", String.valueOf(command.id())));
        }
        var trip = tripOpt.get();

        var companyId = securityContext.currentCompanyId();
        if (companyId != null && !companyId.equals(trip.getCompanyId())) {
            return Result.failure(ApplicationError.notFound("DrivingSession", String.valueOf(command.id())));
        }

        if (command.status() != null) {
            if (trip.getStatus() != TripStatus.IN_PROGRESS) {
                return Result.failure(ApplicationError.conflict("DrivingSession", "Driving session is already closed"));
            }
            trip.setStatus(command.status());
            trip.setEndTime(Instant.now().toString());
            // TODO: trigger PerformanceMetric calculation for this closed session (fatigue score,
            // alerts count, average heart rate) once the metrics-computation service exists.
        }

        return Result.success(tripRepository.save(trip));
    }
}
