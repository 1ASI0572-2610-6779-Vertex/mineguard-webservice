package com.mineguard.platform.iam.application.internal.commandservices;

import com.mineguard.platform.iam.application.commandservices.DeviceCommandService;
import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.domain.model.commands.RegisterDeviceCommand;
import com.mineguard.platform.iam.domain.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Implements smart-band device registration (idempotent by deviceId). */
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final DeviceRepository deviceRepository;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Optional<Device> handle(RegisterDeviceCommand command) {
        if (deviceRepository.existsByDeviceId(command.deviceId())) {
            return deviceRepository.findByDeviceId(command.deviceId());
        }
        var device = new Device(command.deviceId(), command.apiKey(), command.driverId());
        return Optional.of(deviceRepository.save(device));
    }
}
