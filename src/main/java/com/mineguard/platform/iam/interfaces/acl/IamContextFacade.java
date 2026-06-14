package com.mineguard.platform.iam.interfaces.acl;

import com.mineguard.platform.iam.application.commandservices.DeviceCommandService;
import com.mineguard.platform.iam.application.queryservices.DeviceQueryService;
import com.mineguard.platform.iam.domain.model.commands.RegisterDeviceCommand;
import com.mineguard.platform.iam.domain.model.queries.GetDeviceByDeviceIdQuery;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Anti-corruption layer exposing IAM device-authentication capabilities to other
 * bounded contexts (notably {@code monitoring} for the smart-band edge ingestion).
 */
@Service
public class IamContextFacade {
    private final DeviceQueryService deviceQueryService;
    private final DeviceCommandService deviceCommandService;

    public IamContextFacade(DeviceQueryService deviceQueryService, DeviceCommandService deviceCommandService) {
        this.deviceQueryService = deviceQueryService;
        this.deviceCommandService = deviceCommandService;
    }

    /** Validates a device's credentials (device id + API key). */
    public boolean authenticateDevice(String deviceId, String apiKey) {
        return deviceQueryService.handle(new GetDeviceByDeviceIdQuery(deviceId))
                .map(device -> device.authenticatesWith(apiKey))
                .orElse(false);
    }

    /** Returns the driver id linked to a device, when present. */
    public Optional<Long> getDriverIdByDeviceId(String deviceId) {
        return deviceQueryService.handle(new GetDeviceByDeviceIdQuery(deviceId))
                .map(device -> device.getDriverId());
    }

    /** Returns true if a device with the given id exists. */
    public boolean existsDevice(String deviceId) {
        return deviceQueryService.handle(new GetDeviceByDeviceIdQuery(deviceId)).isPresent();
    }

    /** Registers (or seeds) a device, returning its persistence id. */
    public Optional<Long> registerDevice(String deviceId, String apiKey, Long driverId) {
        return deviceCommandService.handle(new RegisterDeviceCommand(deviceId, apiKey, driverId))
                .map(device -> device.getId());
    }
}
