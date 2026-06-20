package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import java.util.List;
public interface SubscriptionRepository { Subscription save(Subscription s); List<Subscription> findAll(); long count(); }
