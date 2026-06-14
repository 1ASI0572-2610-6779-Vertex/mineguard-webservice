package com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.DevicePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DevicePersistenceRepository extends JpaRepository<DevicePersistenceEntity, Long> {
    Optional<DevicePersistenceEntity> findByDeviceId(String deviceId);
    boolean existsByDeviceId(String deviceId);
}
