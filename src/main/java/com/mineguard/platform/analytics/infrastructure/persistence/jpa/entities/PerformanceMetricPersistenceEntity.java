package com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="performance_metrics") @Getter @Setter @NoArgsConstructor
public class PerformanceMetricPersistenceEntity extends AuditableAbstractPersistenceEntity {
    private Long driverId; private Long tripId; private Long vehicleId; private int fatigueEvents; private int alertsCount;
    private double averageHeartRate; private double riskScore; @Column(length=40) private String calculatedAt;
}
