package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {}
