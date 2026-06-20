package com.mineguard.platform.assets.domain.model.valueobjects;

/** Lifecycle status of a trip. */
public enum TripStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static TripStatus fromSerialized(String value) {
        return value == null ? SCHEDULED : TripStatus.valueOf(value.trim().toUpperCase());
    }
}
