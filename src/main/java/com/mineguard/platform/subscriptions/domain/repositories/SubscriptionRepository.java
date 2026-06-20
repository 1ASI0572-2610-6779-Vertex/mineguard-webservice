package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import java.util.List;
import java.util.Optional;
public interface SubscriptionRepository {
    Subscription save(Subscription subscription);
    Optional<Subscription> findById(Long id);
    List<Subscription> findAll();
    List<Subscription> findByUserId(Long userId);
    List<Subscription> findByStatus(SubscriptionStatus status);
    List<Subscription> findAllByCompanyId(Long companyId);
}
