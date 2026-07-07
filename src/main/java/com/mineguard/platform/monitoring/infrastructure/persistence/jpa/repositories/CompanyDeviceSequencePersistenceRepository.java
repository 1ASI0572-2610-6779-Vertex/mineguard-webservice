package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.CompanyDeviceSequencePersistenceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyDeviceSequencePersistenceRepository
        extends JpaRepository<CompanyDeviceSequencePersistenceEntity, Long> {

    /**
     * Loads the company's counter row with a {@code SELECT ... FOR UPDATE} pessimistic write lock so
     * two concurrent device-linking transactions for the same company are serialized and can never
     * hand out the same deviceId.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CompanyDeviceSequencePersistenceEntity> findByCompanyId(Long companyId);
}
