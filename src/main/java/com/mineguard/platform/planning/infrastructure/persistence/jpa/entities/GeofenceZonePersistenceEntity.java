package com.mineguard.platform.planning.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="geofence_zones") @Getter @Setter @NoArgsConstructor
public class GeofenceZonePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="zone_name", length=120) private String zoneName; @Column(name="zone_type", length=40) private String zoneType; @Column(length=20) private String status;
}
