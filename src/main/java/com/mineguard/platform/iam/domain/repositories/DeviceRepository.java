package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.aggregates.Device;

import java.util.Optional;

/** Domain repository port for {@link Device} aggregates. */
public interface DeviceRepository {
    Device save(Device device);
    Optional<Device> findByDeviceId(String deviceId);
    boolean existsByDeviceId(String deviceId);
}
