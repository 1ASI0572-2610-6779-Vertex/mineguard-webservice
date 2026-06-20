package com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;

/** Translates between {@link Device} aggregates and {@link DevicePersistenceEntity}. */
public final class DevicePersistenceAssembler {
    private DevicePersistenceAssembler() {
    }

    public static Device toDomain(DevicePersistenceEntity entity) {
        if (entity == null) return null;
        var device = new Device(entity.getDeviceId(), entity.getApiKey(), entity.getDriverId());
        device.setId(entity.getId());
        return device;
    }

    public static DevicePersistenceEntity toEntity(Device device) {
        var entity = new DevicePersistenceEntity();
        entity.setId(device.getId());
        entity.setDeviceId(device.getDeviceId());
        entity.setApiKey(device.getApiKey());
        entity.setDriverId(device.getDriverId());
        return entity;
    }
}
