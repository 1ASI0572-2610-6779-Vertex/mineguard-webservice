package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class DashboardTrend extends AbstractDomainAggregateRoot<DashboardTrend> {
    @Setter private Long id; @Setter private String hour; @Setter private int alerts; @Setter private int incidents;
    public DashboardTrend() {}
    public DashboardTrend(String hour,int alerts,int incidents){this.hour=hour;this.alerts=alerts;this.incidents=incidents;}
}
