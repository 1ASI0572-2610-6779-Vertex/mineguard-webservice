package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class Company extends AbstractDomainAggregateRoot<Company> {
    /** Default plan assigned when none is supplied — keeps existing rows/DB compatible. */
    public static final String DEFAULT_SUBSCRIPTION_PLAN = "STANDARD";
    @Setter private Long id; @Setter private String name;
    /** M2M credential for the IoT edge ingestion endpoint. Unique per company — never shared platform-wide. */
    @Setter private String edgeApiKey;
    /** Descriptive-only subscription tier (STARTER/STANDARD/ENTERPRISE). No limits are enforced from it. */
    @Setter private String subscriptionPlan = DEFAULT_SUBSCRIPTION_PLAN;
    public Company() {}
    public Company(String name){this.name=name;}
    public Company(String name, String edgeApiKey){this.name=name;this.edgeApiKey=edgeApiKey;}
    public Company(String name, String edgeApiKey, String subscriptionPlan){
        this.name=name;this.edgeApiKey=edgeApiKey;this.subscriptionPlan=normalizePlan(subscriptionPlan);
    }
    /** Falls back to the default plan for null/blank input so legacy callers stay compatible. */
    public static String normalizePlan(String plan){
        return plan==null||plan.isBlank()?DEFAULT_SUBSCRIPTION_PLAN:plan;
    }
}
