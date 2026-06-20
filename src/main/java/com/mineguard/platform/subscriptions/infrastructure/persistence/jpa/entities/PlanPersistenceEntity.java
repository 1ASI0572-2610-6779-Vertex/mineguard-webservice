package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="plans") @Getter @Setter @NoArgsConstructor
public class PlanPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(length=120, nullable=false) private String name; private double price; private int durationDays;
}
