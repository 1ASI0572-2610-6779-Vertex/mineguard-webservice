package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AnalyticsInsight extends AbstractDomainAggregateRoot<AnalyticsInsight> {
    @Setter private Long id; @Setter private String title; @Setter private String description; @Setter private String className;
    public AnalyticsInsight() {}
    public AnalyticsInsight(String title,String description,String className){this.title=title;this.description=description;this.className=className;}
}
