package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Company extends AbstractDomainAggregateRoot<Company> {
    @Setter private Long id; @Setter private String name;
    public Company() {}
    public Company(String name){this.name=name;}
}
