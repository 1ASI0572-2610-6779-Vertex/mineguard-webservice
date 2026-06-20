package com.mineguard.platform.planning.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="zone_boundaries") @Getter @Setter @NoArgsConstructor
public class ZoneBoundaryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="zone_id") private Long zoneId; private double latitude; private double longitude; @Column(name="point_order") private int pointOrder;
}
