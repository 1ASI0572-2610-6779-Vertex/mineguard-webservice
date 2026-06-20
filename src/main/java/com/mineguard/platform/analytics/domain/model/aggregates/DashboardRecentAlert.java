package com.mineguard.platform.analytics.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class DashboardRecentAlert extends AbstractDomainAggregateRoot<DashboardRecentAlert> {
    @Setter private Long id; @Setter private String alertCode; @Setter private String severity; @Setter private String category;
    @Setter private String driverName; @Setter private String vehicleCode; @Setter private String vehicleType; @Setter private String route; @Setter private String time; @Setter private String status;
    public DashboardRecentAlert() {}
    public DashboardRecentAlert(String alertCode,String severity,String category,String driverName,String vehicleCode,String vehicleType,String route,String time,String status){
        this.alertCode=alertCode;this.severity=severity;this.category=category;this.driverName=driverName;this.vehicleCode=vehicleCode;this.vehicleType=vehicleType;this.route=route;this.time=time;this.status=status;}
}
