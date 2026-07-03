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
            case "active", "operational", "available" -> OPERATIONAL;
            case "in_transit", "in_use", "in-transit" -> IN_TRANSIT;
            case "maintenance" -> MAINTENANCE;
            case "inactive" -> INACTIVE;
            case "restricted_route", "restricted" -> RESTRICTED_ROUTE;
            case "alert" -> ALERT;
            default -> {
                // Unknown labels must not break the request with a 400; try an exact
                // enum match and fall back to OPERATIONAL rather than throwing.
                try {
                    yield VehicleStatus.valueOf(value.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    yield OPERATIONAL;
                }
            }
        };
    }
}
