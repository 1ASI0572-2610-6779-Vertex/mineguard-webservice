package com.mineguard.platform.analytics.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="reports") @Getter @Setter @NoArgsConstructor
public class ReportPersistenceEntity extends AuditableAbstractPersistenceEntity {
    private Long incidentId; private Long alertId; private Long userId; private Long metricId;
    @Column(length=60) private String reportType; @Column(name="generated_at", length=40) private String generatedAt; @Column(length=500) private String description;
}
