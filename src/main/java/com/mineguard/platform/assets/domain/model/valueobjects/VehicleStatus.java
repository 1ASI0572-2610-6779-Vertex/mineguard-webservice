package com.mineguard.platform.assets.domain.model.valueobjects;

/** Operational status of a vehicle as shown in the web fleet inventory. */
public enum VehicleStatus {
    OPERATIONAL, IN_TRANSIT, MAINTENANCE, ALERT, INACTIVE, RESTRICTED_ROUTE;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static VehicleStatus fromSerialized(String value) {
        if (value == null) return OPERATIONAL;
        return switch (value.trim().toLowerCase()) {
            case "active", "operational" -> OPERATIONAL;
            case "in_transit" -> IN_TRANSIT;
            case "inactive" -> INACTIVE;
            case "restricted_route" -> RESTRICTED_ROUTE;
            case "alert" -> ALERT;
            default -> VehicleStatus.valueOf(value.trim().toUpperCase());
        };
    }
}
