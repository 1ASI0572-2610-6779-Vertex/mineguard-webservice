package com.mineguard.platform.iam.application.commandservices;

import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.domain.model.commands.RegisterDeviceCommand;

import java.util.Optional;

/** Command service port for smart-band device registration. */
public interface DeviceCommandService {
    Optional<Device> handle(RegisterDeviceCommand command);
}
