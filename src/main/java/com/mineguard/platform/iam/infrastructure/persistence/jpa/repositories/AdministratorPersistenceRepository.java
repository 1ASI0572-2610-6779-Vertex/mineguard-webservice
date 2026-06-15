package com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.AdministratorPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorPersistenceRepository extends JpaRepository<AdministratorPersistenceEntity, Long> {
    boolean existsByEmail(String email);
}