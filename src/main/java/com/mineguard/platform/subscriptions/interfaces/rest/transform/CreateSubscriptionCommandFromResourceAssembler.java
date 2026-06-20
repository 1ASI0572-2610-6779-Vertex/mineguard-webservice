package com.mineguard.platform.subscriptions.interfaces.rest.transform;
import com.mineguard.platform.subscriptions.domain.model.commands.CreateSubscriptionCommand;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.CreateSubscriptionResource;
public final class CreateSubscriptionCommandFromResourceAssembler {
    private CreateSubscriptionCommandFromResourceAssembler() {}
    public static CreateSubscriptionCommand toCommandFromResource(CreateSubscriptionResource resource) {
        return new CreateSubscriptionCommand(resource.userId(), resource.planId(), resource.autoRenew(),
                resource.paymentMethodType(), resource.paymentMethodDetails(), resource.startDate(), resource.endDate());
    }
}
