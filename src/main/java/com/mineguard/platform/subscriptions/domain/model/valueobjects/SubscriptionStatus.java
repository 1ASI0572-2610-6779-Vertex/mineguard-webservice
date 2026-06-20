package com.mineguard.platform.subscriptions.domain.model.valueobjects;
import java.util.Locale;
public enum SubscriptionStatus {
    PENDING, ACTIVE, CANCELED, EXPIRED;
    public String toSerialized() {
        return name().toLowerCase(Locale.ROOT);
    }
    public static SubscriptionStatus fromSerialized(String value) {
        if (value == null || value.isBlank()) return PENDING;
        return SubscriptionStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
