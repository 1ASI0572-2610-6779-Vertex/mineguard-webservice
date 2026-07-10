package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Priority of an operational alert. */
public enum AlertPriority {
    CRITICAL, WARNING, HIGH, MEDIUM, LOW;

    /**
     * Severity ordering, highest first. Declaration order is NOT severity order
     * ({@code WARNING} is declared before {@code HIGH}), so never compare these by {@code ordinal()}.
     */
    public int severityRank() {
        return switch (this) {
            case CRITICAL -> 4;
            case HIGH -> 3;
            case WARNING -> 2;
            case MEDIUM -> 1;
            case LOW -> 0;
        };
    }

    /** True when {@code this} describes a more dangerous situation than {@code other}. */
    public boolean isMoreSevereThan(AlertPriority other) {
        return other == null || severityRank() > other.severityRank();
    }

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertPriority fromSerialized(String value) {
        if (value == null) return WARNING;
        return AlertPriority.valueOf(value.trim().toUpperCase());
    }
}
