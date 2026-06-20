package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.mineguard.platform.subscriptions.domain.repositories.SubscriptionRepository;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {
    private final SubscriptionPersistenceRepository repository;
    public SubscriptionRepositoryImpl(SubscriptionPersistenceRepository repository) {
        this.repository = repository;
    }
    @Override
    public Subscription save(Subscription subscription) {
        return SubscriptionPersistenceAssembler.toDomain(repository.save(SubscriptionPersistenceAssembler.toEntity(subscription)));
    }
    @Override
    public Optional<Subscription> findById(Long id) {
        return repository.findById(id).map(SubscriptionPersistenceAssembler::toDomain);
    }
    @Override
    public List<Subscription> findAll() {
        return repository.findAll().stream().map(SubscriptionPersistenceAssembler::toDomain).toList();
    }
    @Override
    public List<Subscription> findByUserId(Long userId) {
        return repository.findByUserId(userId).stream().map(SubscriptionPersistenceAssembler::toDomain).toList();
    }
    @Override
    public List<Subscription> findByStatus(SubscriptionStatus status) {
        return repository.findByStatus(status).stream().map(SubscriptionPersistenceAssembler::toDomain).toList();
    }
    @Override
    public List<Subscription> findAllByCompanyId(Long companyId) {
        return repository.findByCompanyId(companyId).stream().map(SubscriptionPersistenceAssembler::toDomain).toList();
    }
}
