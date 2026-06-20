package com.mineguard.platform.subscriptions.interfaces.rest.transform;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.SubscriptionResource;
public final class SubscriptionResourceFromEntityAssembler {
    private SubscriptionResourceFromEntityAssembler() {}
    public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
        return new SubscriptionResource(subscription.getId(), subscription.getCompanyId(), subscription.getUserId(),
                subscription.getPlan() != null ? PlanResourceFromEntityAssembler.toResourceFromEntity(subscription.getPlan()) : null,
                subscription.getStatus() != null ? subscription.getStatus().toSerialized() : null,
                subscription.getStartDate(), subscription.getEndDate(), subscription.getAutoRenew(),
                subscription.getPayments() != null ? subscription.getPayments().stream().map(PaymentResourceFromEntityAssembler::toResourceFromEntity).toList() : java.util.List.of(),
                subscription.getPaymentMethod() != null ? subscription.getPaymentMethod().getType() : null,
                subscription.getPaymentMethod() != null ? subscription.getPaymentMethod().getDetails() : null);
    }
}
