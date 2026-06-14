package com.mineguard.platform.assets.domain.model.valueobjects;

/** Operational status of a vehicle as shown in the web fleet inventory. */
public enum VehicleStatus {
    OPERATIONAL, MAINTENANCE, ALERT;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static VehicleStatus fromSerialized(String value) {
        return value == null ? OPERATIONAL : VehicleStatus.valueOf(value.trim().toUpperCase());
    }
}
