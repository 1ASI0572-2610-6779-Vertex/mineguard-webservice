package com.mineguard.platform.subscriptions.domain.model.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class PaymentMethod {
    private Long id;
    private String type;
    private String details;
    public PaymentMethod(String type, String details) {
        this.type = type;
        this.details = details;
    }
}
