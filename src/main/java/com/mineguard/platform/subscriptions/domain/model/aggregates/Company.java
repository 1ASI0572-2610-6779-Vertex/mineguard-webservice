package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Company extends AbstractDomainAggregateRoot<Company> {
    @Setter private Long id; @Setter private String name;
    /** M2M credential for the IoT edge ingestion endpoint. Unique per company — never shared platform-wide. */
    @Setter private String edgeApiKey;
    public Company() {}
    public Company(String name){this.name=name;}
    public Company(String name, String edgeApiKey){this.name=name;this.edgeApiKey=edgeApiKey;}
}
