package com.mineguard.platform.subscriptions.domain.model.valueobjects;
import java.util.Locale;
public enum PaymentStatus {
    PENDING, SUCCESSFUL, FAILED, RETRYING;
    public String toSerialized() {
        return name().toLowerCase(Locale.ROOT);
    }
    public static PaymentStatus fromSerialized(String value) {
        if (value == null || value.isBlank()) return PENDING;
        return PaymentStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
