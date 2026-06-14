package com.mineguard.platform.planning.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter; import lombok.Setter;
@Getter public class GeofenceZone extends AbstractDomainAggregateRoot<GeofenceZone> {
    @Setter private Long id; @Setter private String zoneName; @Setter private String zoneType; @Setter private String status;
    public GeofenceZone() {}
    public GeofenceZone(String zoneName,String zoneType,String status){this.zoneName=zoneName;this.zoneType=zoneType;this.status=status;}
}
