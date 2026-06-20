package com.mineguard.platform.subscriptions.interfaces.rest.transform;
import com.mineguard.platform.subscriptions.domain.model.entities.Payment;
import com.mineguard.platform.subscriptions.interfaces.rest.resources.PaymentResource;
public final class PaymentResourceFromEntityAssembler {
    private PaymentResourceFromEntityAssembler() {}
    public static PaymentResource toResourceFromEntity(Payment payment) {
        return new PaymentResource(payment.getId(),
                payment.getAmount() != null ? payment.getAmount().amount() : null,
                payment.getAmount() != null ? payment.getAmount().currency() : null,
                payment.getStatus() != null ? payment.getStatus().toSerialized() : null,
                payment.getCreatedAt());
    }
}
