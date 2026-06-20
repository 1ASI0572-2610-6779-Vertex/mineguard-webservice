package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Type of an operational alert. */
public enum AlertType {
    COLLISION, IMMINENT_COLLISION, FATIGUE, PROXIMITY_COLLISION, PROXIMITY;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertType fromSerialized(String value) {
        if (value == null) return COLLISION;
        return switch (value.trim().toLowerCase()) {
            case "fatigue_risk", "fatigue" -> FATIGUE;
            case "proximity_collision", "collision" -> PROXIMITY_COLLISION;
            case "speed_excess", "restricted_zone_entry", "high_heart_rate", "connection_lost",
                    "sensor_maintenance" -> PROXIMITY;
            case "imminent_collision" -> IMMINENT_COLLISION;
            default -> AlertType.valueOf(value.trim().toUpperCase());
        };
    }
}
