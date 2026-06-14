package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Priority of an operational alert. */
public enum AlertPriority {
    CRITICAL, WARNING, HIGH, MEDIUM, LOW;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertPriority fromSerialized(String value) {
        if (value == null) return WARNING;
        return AlertPriority.valueOf(value.trim().toUpperCase());
    }
}
