package com.mineguard.platform.subscriptions.domain.model.entities;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.Money;
import com.mineguard.platform.subscriptions.domain.model.valueobjects.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    private Long id;
    private Money amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    public Payment(Money amount, PaymentStatus status, LocalDateTime createdAt) {
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }
    public void markSuccessful() {
        this.status = PaymentStatus.SUCCESSFUL;
    }
    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
    public void retry() {
        this.status = PaymentStatus.SUCCESSFUL;
    }
}
