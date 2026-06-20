package com.mineguard.platform.planning.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class ZoneBoundary extends AbstractDomainAggregateRoot<ZoneBoundary> {
    @Setter private Long id; @Setter private Long zoneId; @Setter private double latitude; @Setter private double longitude; @Setter private int pointOrder;
    public ZoneBoundary() {}
    public ZoneBoundary(Long zoneId,double latitude,double longitude,int pointOrder){this.zoneId=zoneId;this.latitude=latitude;this.longitude=longitude;this.pointOrder=pointOrder;}
}
