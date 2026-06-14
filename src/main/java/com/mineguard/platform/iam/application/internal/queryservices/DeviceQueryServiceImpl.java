package com.mineguard.platform.iam.application.internal.queryservices;

import com.mineguard.platform.iam.application.queryservices.DeviceQueryService;
import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.domain.model.queries.GetDeviceByDeviceIdQuery;
import com.mineguard.platform.iam.domain.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceQueryServiceImpl implements DeviceQueryService {
    private final DeviceRepository deviceRepository;

    public DeviceQueryServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Optional<Device> handle(GetDeviceByDeviceIdQuery query) {
        return deviceRepository.findByDeviceId(query.deviceId());
    }
}
