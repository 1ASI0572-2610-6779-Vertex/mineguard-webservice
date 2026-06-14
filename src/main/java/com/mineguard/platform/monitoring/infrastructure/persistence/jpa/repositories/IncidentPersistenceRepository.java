package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentPersistenceRepository extends JpaRepository<IncidentPersistenceEntity, Long> {
}
