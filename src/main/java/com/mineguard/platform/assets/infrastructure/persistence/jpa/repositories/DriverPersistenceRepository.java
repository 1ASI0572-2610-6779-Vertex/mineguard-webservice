package com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.assets.domain.model.valueobjects.ShiftStatus;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverPersistenceRepository extends JpaRepository<DriverPersistenceEntity, Long> {
    long countByShiftStatus(ShiftStatus status);
    java.util.List<DriverPersistenceEntity> findAllByCompanyId(Long companyId);
}
