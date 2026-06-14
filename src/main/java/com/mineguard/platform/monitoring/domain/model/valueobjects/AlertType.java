package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Type of an operational alert. */
public enum AlertType {
    COLLISION, IMMINENT_COLLISION, FATIGUE, PROXIMITY_COLLISION, PROXIMITY;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertType fromSerialized(String value) {
        if (value == null) return COLLISION;
        return AlertType.valueOf(value.trim().toUpperCase());
    }
}
