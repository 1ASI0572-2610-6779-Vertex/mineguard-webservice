package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.commandservices.SensorCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.model.commands.CreateSensorCommand;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class SensorCommandServiceImpl implements SensorCommandService {

    private final SensorRepository sensorRepository;
    private final VehicleRepository vehicleRepository;

    public SensorCommandServiceImpl(SensorRepository sensorRepository, VehicleRepository vehicleRepository) {
        this.sensorRepository = sensorRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Result<Sensor, ApplicationError> handle(CreateSensorCommand command) {
        // Tenant must be resolvable from the JWT — an unauthenticated caller cannot own sensors.
        if (command.companyId() == null) {
            return Result.failure(ApplicationError.validationError("companyId",
                    "No authenticated company — a valid JWT is required to create a sensor"));
        }
        if (command.deviceId() == null || command.deviceId().isBlank()) {
            return Result.failure(ApplicationError.validationError("deviceId", "deviceId is required"));
        }
        if (command.vehicleId() == null) {
            return Result.failure(ApplicationError.validationError("vehicleId", "vehicleId is required"));
        }

        // The vehicle must exist AND belong to the caller's company (cross-tenant mount is blocked).
        var vehicle = vehicleRepository.findById(command.vehicleId());
        if (vehicle.isEmpty() || !command.companyId().equals(vehicle.get().getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.vehicleId())));
        }

        // One unified device per vehicle — a MineGuard device already bundles all sensors, so a
        // vehicle can be linked to a single device only. Reject a second one.
        if (sensorRepository.existsByVehicleIdAndCompanyId(command.vehicleId(), command.companyId())) {
            return Result.failure(ApplicationError.conflict("Sensor",
                    "Vehicle " + command.vehicleId() + " already has a device linked"));
        }

        // device_id is unique within a company — reject a duplicate before it collides at ingestion time.
        if (sensorRepository.findByDeviceIdAndCompanyId(command.deviceId(), command.companyId()).isPresent()) {
            return Result.failure(ApplicationError.conflict("Sensor",
                    "A device with device_id '" + command.deviceId() + "' is already registered for this company"));
        }

        var status = (command.status() == null || command.status().isBlank()) ? "active" : command.status();
        var sensorType = (command.sensorType() == null || command.sensorType().isBlank())
                ? "MineGuard Device" : command.sensorType();
        var sensor = new Sensor(command.vehicleId(), sensorType, status);
        sensor.setDeviceId(command.deviceId());
        sensor.setCompanyId(command.companyId());
        return Result.success(sensorRepository.save(sensor));
    }
}