package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class AnalyticsFatigueBar extends AbstractDomainAggregateRoot<AnalyticsFatigueBar> {
    @Setter private Long id; @Setter private Long driverId; @Setter private String driverName; @Setter private int fatigueEvents; @Setter private int width;
    public AnalyticsFatigueBar() {}
    public AnalyticsFatigueBar(Long driverId,String driverName,int fatigueEvents,int width){this.driverId=driverId;this.driverName=driverName;this.fatigueEvents=fatigueEvents;this.width=width;}
}
