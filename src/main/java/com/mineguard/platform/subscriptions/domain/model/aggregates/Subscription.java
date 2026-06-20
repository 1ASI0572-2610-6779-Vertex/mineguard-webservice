package com.mineguard.platform.subscriptions.domain.model.aggregates;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.mineguard.platform.subscriptions.domain.model.entities.Payment;
import com.mineguard.platform.subscriptions.domain.model.entities.PaymentMethod;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.SubscriptionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
public class Subscription extends AbstractDomainAggregateRoot<Subscription> {
    private Long id;
    private Long companyId;
    private Long userId;
    private Plan plan;
    private SubscriptionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private List<Payment> payments = new ArrayList<>();
    private PaymentMethod paymentMethod;
    public Subscription(Long userId, Plan plan, Boolean autoRenew, PaymentMethod paymentMethod) {
        this.userId = userId;
        this.plan = plan;
        this.autoRenew = autoRenew != null && autoRenew;
        this.paymentMethod = paymentMethod;
        this.status = SubscriptionStatus.PENDING;
        this.payments = new ArrayList<>();
    }
    public void activate() {
        if (startDate == null) startDate = LocalDateTime.now();
        if (endDate == null) endDate = startDate.plusDays(plan != null ? plan.billingCycleDays() : 30);
        status = SubscriptionStatus.ACTIVE;
    }
    public void cancel() {
        status = SubscriptionStatus.CANCELED;
        autoRenew = false;
    }
    public void renew() {
        if (startDate == null) startDate = LocalDateTime.now();
        var days = plan != null ? plan.billingCycleDays() : 30;
        endDate = (endDate != null ? endDate : startDate).plusDays(days);
        status = SubscriptionStatus.ACTIVE;
        autoRenew = true;
    }
    public void processPayment(Payment payment) {
        if (payment == null) return;
        payment.markSuccessful();
        if (payments == null) payments = new ArrayList<>();
        payments.add(payment);
        if (status != SubscriptionStatus.CANCELED) status = SubscriptionStatus.ACTIVE;
    }
    public void retryPayment(Long paymentId) {
        if (payments == null || paymentId == null) return;
        payments.stream().filter(payment -> Objects.equals(payment.getId(), paymentId)).findFirst().ifPresent(Payment::retry);
    }
    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }
    public void updatePaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
