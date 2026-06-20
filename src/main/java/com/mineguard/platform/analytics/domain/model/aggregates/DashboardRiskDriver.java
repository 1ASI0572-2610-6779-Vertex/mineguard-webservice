package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class DashboardRiskDriver extends AbstractDomainAggregateRoot<DashboardRiskDriver> {
    @Setter private Long id; @Setter private Long driverId; @Setter private String driverName; @Setter private String vehicleType; @Setter private double riskScore;
    public DashboardRiskDriver() {}
    public DashboardRiskDriver(Long driverId,String driverName,String vehicleType,double riskScore){this.driverId=driverId;this.driverName=driverName;this.vehicleType=vehicleType;this.riskScore=riskScore;}
}
