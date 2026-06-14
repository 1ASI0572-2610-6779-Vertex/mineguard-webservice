package com.mineguard.platform.planning.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="zone_permissions") @Getter @Setter @NoArgsConstructor
public class ZonePermissionPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="zone_id") private Long zoneId; @Column(name="driver_id") private Long driverId; @Column(name="permission_type", length=40) private String permissionType;
    @Column(name="start_date", length=40) private String startDate; @Column(name="end_date", length=40) private String endDate; @Column(length=20) private String status;
}
