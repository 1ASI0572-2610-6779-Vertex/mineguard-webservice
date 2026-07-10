package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Type of an operational alert. */
public enum AlertType {
    COLLISION, IMMINENT_COLLISION, FATIGUE, PROXIMITY_COLLISION, PROXIMITY,
    HIGH_HEART_RATE, CARDIAC_ARREST, EMERGENCY_SOS;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static AlertType fromSerialized(String value) {
        if (value == null) return COLLISION;
        return switch (value.trim().toLowerCase()) {
            case "fatigue_risk", "fatigue" -> FATIGUE;
            case "proximity_collision", "collision" -> PROXIMITY_COLLISION;
            case "high_heart_rate" -> HIGH_HEART_RATE;
            case "cardiac_arrest" -> CARDIAC_ARREST;
            case "emergency_sos", "sos" -> EMERGENCY_SOS;
            case "speed_excess", "restricted_zone_entry", "connection_lost",
                    "sensor_maintenance" -> PROXIMITY;
            case "imminent_collision" -> IMMINENT_COLLISION;
            default -> AlertType.valueOf(value.trim().toUpperCase());
        };
    }
}
