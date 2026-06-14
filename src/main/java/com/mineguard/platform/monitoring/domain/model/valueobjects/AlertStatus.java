package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Lifecycle status of an operational alert. */
public enum AlertStatus {
    ACTIVE, RESOLVED, FALSE_ALARM;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertStatus fromSerialized(String value) {
        if (value == null) return ACTIVE;
        return AlertStatus.valueOf(value.trim().toUpperCase());
    }
}
