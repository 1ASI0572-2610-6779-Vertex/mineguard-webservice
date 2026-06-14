package com.mineguard.platform.planning.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class ZonePermission extends AbstractDomainAggregateRoot<ZonePermission> {
    @Setter private Long id; @Setter private Long zoneId; @Setter private Long driverId; @Setter private String permissionType;
    @Setter private String startDate; @Setter private String endDate; @Setter private String status;
    public ZonePermission() {}
    public ZonePermission(Long zoneId,Long driverId,String permissionType,String startDate,String endDate,String status){
        this.zoneId=zoneId;this.driverId=driverId;this.permissionType=permissionType;this.startDate=startDate;this.endDate=endDate;this.status=status;}
}
