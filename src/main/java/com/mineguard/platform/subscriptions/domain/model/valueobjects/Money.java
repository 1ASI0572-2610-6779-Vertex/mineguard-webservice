package com.mineguard.platform.subscriptions.domain.model.valueobjects;
import java.math.BigDecimal;
/** Monetary value object used by subscriptions, plans, and payments. */
public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount == null) amount = BigDecimal.ZERO;
        if (currency == null || currency.isBlank()) currency = "USD";
    }
}
