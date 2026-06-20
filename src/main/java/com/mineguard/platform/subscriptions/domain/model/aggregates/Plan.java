package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Plan extends AbstractDomainAggregateRoot<Plan> {
    @Setter private Long id; @Setter private String name; @Setter private double price; @Setter private int durationDays;
    public Plan() {}
    public Plan(String name,double price,int durationDays){this.name=name;this.price=price;this.durationDays=durationDays;}
}
