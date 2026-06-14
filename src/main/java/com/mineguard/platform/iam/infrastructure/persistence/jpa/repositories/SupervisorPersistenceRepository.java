package com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.SupervisorPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupervisorPersistenceRepository extends JpaRepository<SupervisorPersistenceEntity, Long> {
    boolean existsByCorporateId(String corporateId);
}
