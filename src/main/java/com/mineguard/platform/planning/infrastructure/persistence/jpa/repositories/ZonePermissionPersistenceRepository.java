package com.mineguard.platform.planning.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.planning.infrastructure.persistence.jpa.entities.ZonePermissionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ZonePermissionPersistenceRepository extends JpaRepository<ZonePermissionPersistenceEntity, Long> {}
