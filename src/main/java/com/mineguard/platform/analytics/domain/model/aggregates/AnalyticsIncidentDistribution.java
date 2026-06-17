package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AnalyticsIncidentDistribution extends AbstractDomainAggregateRoot<AnalyticsIncidentDistribution> {
    @Setter private Long id; @Setter private String label; @Setter private int count; @Setter private int percent; @Setter private String className;
    public AnalyticsIncidentDistribution() {}
    public AnalyticsIncidentDistribution(String label,int count,int percent,String className){this.label=label;this.count=count;this.percent=percent;this.className=className;}
}
