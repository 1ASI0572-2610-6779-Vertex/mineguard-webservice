package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class PlanPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(nullable = false, length = 120)
    private String name;
    @Column(name = "price_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal priceAmount;
    @Column(name = "price_currency", nullable = false, length = 10)
    private String priceCurrency;
    @Column(name = "billing_cycle", nullable = false, length = 60)
    private String billingCycle;
}
