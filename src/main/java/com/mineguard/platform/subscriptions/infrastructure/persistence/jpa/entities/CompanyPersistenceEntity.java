package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*; import lombok.Getter; import lombok.NoArgsConstructor; import lombok.Setter;
@Entity @Table(name="companies") @Getter @Setter @NoArgsConstructor
public class CompanyPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(length=120, nullable=false) private String name;
    @Column(name = "edge_api_key", length = 64, unique = true) private String edgeApiKey;
    /** Descriptive subscription tier. Nullable in DB; defaulted to STANDARD in the mapping for legacy rows. */
    @Column(name = "subscription_plan", length = 32) private String subscriptionPlan;
}
