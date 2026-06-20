package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="subscriptions") @Getter @Setter @NoArgsConstructor
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name="company_id") private Long companyId; @Column(name="plan_id") private Long planId;
    @Column(name="start_date", length=40) private String startDate; @Column(name="end_date", length=40) private String endDate;
    @Column(length=20) private String status;
}
