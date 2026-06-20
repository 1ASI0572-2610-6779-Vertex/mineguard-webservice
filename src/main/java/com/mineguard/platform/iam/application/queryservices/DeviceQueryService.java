package com.mineguard.platform.iam.application.queryservices;

import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.domain.model.queries.GetDeviceByDeviceIdQuery;

import java.util.Optional;

/** Query service port for devices. */
public interface DeviceQueryService {
    Optional<Device> handle(GetDeviceByDeviceIdQuery query);
}
