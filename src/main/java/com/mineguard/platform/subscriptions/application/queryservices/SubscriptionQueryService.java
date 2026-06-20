package com.mineguard.platform.subscriptions.application.queryservices;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.domain.model.queries.*;
import java.util.List;
import java.util.Optional;
public interface SubscriptionQueryService {
    Optional<Subscription> handle(GetSubscriptionByIdQuery query);
    List<Subscription> handle(GetSubscriptionsByUserQuery query);
    List<Plan> handle(GetPlansQuery query);
}
