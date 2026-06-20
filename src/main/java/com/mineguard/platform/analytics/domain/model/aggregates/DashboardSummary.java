package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class DashboardSummary extends AbstractDomainAggregateRoot<DashboardSummary> {
    @Setter private Long id; @Setter private int activeSensors; @Setter private int totalSensors;
    @Setter private int criticalAlerts; @Setter private int fatigueEvents; @Setter private int activeVehicles; @Setter private int totalDrivers;
    public DashboardSummary() {}
    public DashboardSummary(int activeSensors,int totalSensors,int criticalAlerts,int fatigueEvents,int activeVehicles,int totalDrivers){
        this.activeSensors=activeSensors;this.totalSensors=totalSensors;this.criticalAlerts=criticalAlerts;this.fatigueEvents=fatigueEvents;this.activeVehicles=activeVehicles;this.totalDrivers=totalDrivers;}
}
