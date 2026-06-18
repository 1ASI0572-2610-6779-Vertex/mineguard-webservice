package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class PerformanceMetric extends AbstractDomainAggregateRoot<PerformanceMetric> {
    @Setter private Long id; @Setter private Long driverId; @Setter private Long tripId; @Setter private Long vehicleId;
    @Setter private int fatigueEvents; @Setter private int alertsCount; @Setter private double averageHeartRate; @Setter private double riskScore; @Setter private String calculatedAt;
    public PerformanceMetric() {}
    public PerformanceMetric(Long driverId,Long tripId,Long vehicleId,int fatigueEvents,int alertsCount,double averageHeartRate,double riskScore,String calculatedAt){
        this.driverId=driverId;this.tripId=tripId;this.vehicleId=vehicleId;this.fatigueEvents=fatigueEvents;this.alertsCount=alertsCount;this.averageHeartRate=averageHeartRate;this.riskScore=riskScore;this.calculatedAt=calculatedAt;}
}
