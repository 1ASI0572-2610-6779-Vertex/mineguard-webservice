package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {
    List<SubscriptionPersistenceEntity> findByUserId(Long userId);
    List<SubscriptionPersistenceEntity> findByStatus(SubscriptionStatus status);
    List<SubscriptionPersistenceEntity> findByCompanyId(Long companyId);
}
