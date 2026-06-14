package com.mineguard.platform.assets.domain.model.valueobjects;

/** Shift status of a driver as shown in the drivers directory. */
public enum ShiftStatus {
    ON_SHIFT, OFF_SHIFT, INACTIVE;

    public String toSerialized() {
        return name().toLowerCase();
    }

    public static ShiftStatus fromSerialized(String value) {
        return value == null ? OFF_SHIFT : ShiftStatus.valueOf(value.trim().toUpperCase());
    }
}
