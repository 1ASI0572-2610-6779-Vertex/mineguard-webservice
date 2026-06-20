package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PlanPersistenceRepository extends JpaRepository<PlanPersistenceEntity, Long> {}
