package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Lob
    @Column(name = "plan_json", nullable = false)
    private String planJson;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "auto_renew")
    private Boolean autoRenew;
    @Lob
    @Column(name = "payments_json", nullable = false)
    private String paymentsJson;
    @Lob
    @Column(name = "payment_method_json")
    private String paymentMethodJson;
}
