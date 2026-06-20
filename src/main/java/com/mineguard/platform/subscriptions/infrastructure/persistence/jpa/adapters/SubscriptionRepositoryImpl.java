package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.repositories.SubscriptionRepository;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers.SubscriptionPersistenceAssembler;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories.SubscriptionPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class SubscriptionRepositoryImpl implements SubscriptionRepository {
    private final SubscriptionPersistenceRepository r;
    public SubscriptionRepositoryImpl(SubscriptionPersistenceRepository r){this.r=r;}
    public Subscription save(Subscription s){return SubscriptionPersistenceAssembler.toDomain(r.save(SubscriptionPersistenceAssembler.toEntity(s)));}
    public List<Subscription> findAll(){return r.findAll().stream().map(SubscriptionPersistenceAssembler::toDomain).toList();}
    public long count(){return r.count();}
}
