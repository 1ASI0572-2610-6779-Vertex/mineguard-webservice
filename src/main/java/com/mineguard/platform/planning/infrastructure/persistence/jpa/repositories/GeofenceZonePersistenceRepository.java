package com.mineguard.platform.planning.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.entities.GeofenceZonePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GeofenceZonePersistenceRepository extends JpaRepository<GeofenceZonePersistenceEntity, Long> {}
