package com.mineguard.platform.iam.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.iam.domain.model.aggregates.Device;
import com.mineguard.platform.iam.domain.repositories.DeviceRepository;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers.DevicePersistenceAssembler;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories.DevicePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DeviceRepositoryImpl implements DeviceRepository {
    private final DevicePersistenceRepository repository;

    public DeviceRepositoryImpl(DevicePersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Device save(Device device) {
        var saved = repository.save(DevicePersistenceAssembler.toEntity(device));
        return DevicePersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Device> findByDeviceId(String deviceId) {
        return repository.findByDeviceId(deviceId).map(DevicePersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByDeviceId(String deviceId) {
        return repository.existsByDeviceId(deviceId);
    }
}
