package com.mineguard.platform.subscriptions.interfaces.rest.transform;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.PlanResource;
public final class PlanResourceFromEntityAssembler {
    private PlanResourceFromEntityAssembler() {}
    public static PlanResource toResourceFromEntity(Plan plan) {
        return new PlanResource(plan.getId(), plan.getName(),
                plan.getPrice() != null ? plan.getPrice().amount() : null,
                plan.getPrice() != null ? plan.getPrice().currency() : null,
                plan.getBillingCycle());
    }
}
