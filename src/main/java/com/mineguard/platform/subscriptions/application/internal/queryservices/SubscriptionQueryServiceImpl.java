package com.mineguard.platform.subscriptions.application.internal.queryservices;
import com.mineguard.platform.shared.infrastructure.security.SecurityContextFacade;
import com.mineguard.platform.subscriptions.application.queryservices.SubscriptionQueryService;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.domain.model.queries.GetPlansQuery;
import com.mineguard.platform.subscriptions.domain.model.queries.GetSubscriptionByIdQuery;
import com.mineguard.platform.subscriptions.domain.model.queries.GetSubscriptionsByUserQuery;
import com.mineguard.platform.subscriptions.domain.repositories.PlanRepository;
import com.mineguard.platform.subscriptions.domain.repositories.SubscriptionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
@Service
public class SubscriptionQueryServiceImpl implements SubscriptionQueryService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final SecurityContextFacade securityContext;
    public SubscriptionQueryServiceImpl(SubscriptionRepository subscriptionRepository, PlanRepository planRepository,
                                        SecurityContextFacade securityContext) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.securityContext = securityContext;
    }
    @Override
    public Optional<Subscription> handle(GetSubscriptionByIdQuery query) {
        var companyId = securityContext.currentCompanyId();
        return subscriptionRepository.findById(query.subscriptionId())
                .filter(subscription -> companyId == null || companyId.equals(subscription.getCompanyId()));
    }
    @Override
    public List<Subscription> handle(GetSubscriptionsByUserQuery query) {
        var companyId = securityContext.currentCompanyId();
        if (companyId == null) return List.of();
        return subscriptionRepository.findByUserId(query.userId()).stream()
                .filter(subscription -> companyId.equals(subscription.getCompanyId()))
                .toList();
    }
    @Override
    public List<Plan> handle(GetPlansQuery query) {
        return planRepository.findAll();
    }
}
