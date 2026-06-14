package com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories;

import com.mineguard.platform.assets.infrastructure.persistence.jpa.entities.TripPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripPersistenceRepository extends JpaRepository<TripPersistenceEntity, Long> {
}
