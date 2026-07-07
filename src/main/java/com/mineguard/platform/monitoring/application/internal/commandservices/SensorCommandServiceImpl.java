package com.mineguard.platform.monitoring.application.internal.commandservices;

import com.mineguard.platform.assets.domain.repositories.VehicleRepository;
import com.mineguard.platform.monitoring.application.commandservices.SensorCommandService;
import com.mineguard.platform.monitoring.domain.model.aggregates.Sensor;
import com.mineguard.platform.monitoring.domain.model.commands.CreateSensorCommand;
import com.mineguard.platform.monitoring.domain.model.commands.LinkDeviceCommand;
import com.mineguard.platform.monitoring.domain.model.commands.UpdateSensorCommand;
import com.mineguard.platform.monitoring.domain.repositories.SensorRepository;
import com.mineguard.platform.monitoring.infrastructure.sequence.DeviceIdSequenceGenerator;
import com.mineguard.platform.shared.application.result.ApplicationError;
import com.mineguard.platform.shared.application.result.Result;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class SensorCommandServiceImpl implements SensorCommandService {

    private static final Set<String> ALLOWED_STATUSES =
            Set.of(Sensor.STATUS_ACTIVE, Sensor.STATUS_INACTIVE, Sensor.STATUS_RETIRED);

    private final SensorRepository sensorRepository;
    private final VehicleRepository vehicleRepository;
    private final DeviceIdSequenceGenerator deviceIdSequenceGenerator;

    public SensorCommandServiceImpl(SensorRepository sensorRepository, VehicleRepository vehicleRepository,
                                    DeviceIdSequenceGenerator deviceIdSequenceGenerator) {
        this.sensorRepository = sensorRepository;
        this.vehicleRepository = vehicleRepository;
        this.deviceIdSequenceGenerator = deviceIdSequenceGenerator;
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
        // vehicle can be linked to a single (non-retired) device only. Reject a second one.
        if (sensorRepository.existsActiveByVehicleIdAndCompanyId(command.vehicleId(), command.companyId())) {
            return Result.failure(ApplicationError.conflict("Sensor",
                    "Vehicle " + command.vehicleId() + " already has a device linked"));
        }

        // device_id is unique within a company — reject a duplicate before it collides at ingestion time.
        if (sensorRepository.findByDeviceIdAndCompanyId(command.deviceId(), command.companyId()).isPresent()) {
            return Result.failure(ApplicationError.conflict("Sensor",
                    "A device with device_id '" + command.deviceId() + "' is already registered for this company"));
        }

        var status = (command.status() == null || command.status().isBlank()) ? Sensor.STATUS_ACTIVE : command.status();
        var sensorType = (command.sensorType() == null || command.sensorType().isBlank())
                ? Sensor.MINEGUARD_DEVICE_TYPE : command.sensorType();
        var sensor = new Sensor(command.vehicleId(), sensorType, status);
        sensor.setDeviceId(command.deviceId());
        sensor.setCompanyId(command.companyId());
        return Result.success(sensorRepository.save(sensor));
    }

    @Override
    @Transactional
    public Result<Sensor, ApplicationError> handle(LinkDeviceCommand command) {
        if (command.companyId() == null) {
            return Result.failure(ApplicationError.validationError("companyId",
                    "No authenticated company — a valid JWT is required to link a device"));
        }
        if (command.vehicleId() == null) {
            return Result.failure(ApplicationError.validationError("vehicleId", "vehicleId is required"));
        }

        // Vehicle must exist AND belong to the caller's company (cross-tenant link is blocked → 404).
        var vehicle = vehicleRepository.findById(command.vehicleId());
        if (vehicle.isEmpty() || !command.companyId().equals(vehicle.get().getCompanyId())) {
            return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.vehicleId())));
        }

        // One device per vehicle — reject a second one (409).
        if (sensorRepository.existsActiveByVehicleIdAndCompanyId(command.vehicleId(), command.companyId())) {
            return Result.failure(ApplicationError.conflict("Sensor",
                    "Vehicle " + command.vehicleId() + " already has a device linked"));
        }

        // Server-assigned, per-company sequential deviceId (atomic; never reused).
        long deviceId = nextDeviceId(command.companyId());

        var sensor = new Sensor(command.vehicleId(), Sensor.MINEGUARD_DEVICE_TYPE, Sensor.STATUS_ACTIVE);
        sensor.setDeviceId(String.valueOf(deviceId));
        sensor.setCompanyId(command.companyId());
        return Result.success(sensorRepository.save(sensor));
    }

    @Override
    @Transactional
    public Result<Sensor, ApplicationError> handle(UpdateSensorCommand command) {
        if (command.companyId() == null) {
            return Result.failure(ApplicationError.validationError("companyId",
                    "No authenticated company — a valid JWT is required to update a device"));
        }

        var existing = sensorRepository.findByIdAndCompanyId(command.id(), command.companyId());
        if (existing.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Sensor", String.valueOf(command.id())));
        }
        var sensor = existing.get();

        // Reassignment (move) — preserves the deviceId, only the vehicle association changes.
        if (command.vehicleId() != null && !command.vehicleId().equals(sensor.getVehicleId())) {
            var target = vehicleRepository.findById(command.vehicleId());
            if (target.isEmpty() || !command.companyId().equals(target.get().getCompanyId())) {
                return Result.failure(ApplicationError.notFound("Vehicle", String.valueOf(command.vehicleId())));
            }
            if (sensorRepository.existsActiveByVehicleIdAndCompanyId(command.vehicleId(), command.companyId())) {
                return Result.failure(ApplicationError.conflict("Sensor", "target vehicle already has a device"));
            }
            sensor.setVehicleId(command.vehicleId());
        }

        // Status change — active | inactive | retired.
        if (command.status() != null && !command.status().isBlank()) {
            var status = command.status().trim().toLowerCase();
            if (!ALLOWED_STATUSES.contains(status)) {
                return Result.failure(ApplicationError.validationError("status",
                        "status must be one of active, inactive, retired"));
            }
            sensor.setStatus(status);
        }

        return Result.success(sensorRepository.save(sensor));
    }

    /**
     * Obtains the next sequential deviceId, retrying once on the rare first-insert race where two
     * transactions create a company's counter row simultaneously (see {@link DeviceIdSequenceGenerator}).
     */
    private long nextDeviceId(Long companyId) {
        try {
            return deviceIdSequenceGenerator.next(companyId);
        } catch (DataIntegrityViolationException firstInsertRace) {
            return deviceIdSequenceGenerator.next(companyId);
        }
    }
}
