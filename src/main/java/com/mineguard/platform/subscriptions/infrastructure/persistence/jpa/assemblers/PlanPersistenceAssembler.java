package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.Money;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
public final class PlanPersistenceAssembler {
    private PlanPersistenceAssembler() {}
    public static Plan toDomain(PlanPersistenceEntity entity) {
        if (entity == null) return null;
        var plan = new Plan(entity.getName(), new Money(entity.getPriceAmount(), entity.getPriceCurrency()), entity.getBillingCycle());
        plan.setId(entity.getId());
        return plan;
    }
    public static PlanPersistenceEntity toEntity(Plan plan) {
        var entity = new PlanPersistenceEntity();
        entity.setId(plan.getId());
        entity.setName(plan.getName());
        entity.setPriceAmount(plan.getPrice() != null ? plan.getPrice().amount() : null);
        entity.setPriceCurrency(plan.getPrice() != null ? plan.getPrice().currency() : null);
        entity.setBillingCycle(plan.getBillingCycle());
        return entity;
    }
}
