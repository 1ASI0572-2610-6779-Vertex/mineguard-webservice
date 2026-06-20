package com.mineguard.platform.iam.domain.model.valueobjects;

/**
 * Access status of a supervisor account, as consumed by the web administration view.
 */
public enum AccessStatus {
    ACTIVE,
    LOCKED,
    INACTIVE;

    /** Serialized form expected by the frontend (lowercase). */
    public String toSerialized() {
        return name().toLowerCase();
    }

    /** Parses the serialized lowercase form coming from the frontend. */
    public static AccessStatus fromSerialized(String value) {
        return value == null ? ACTIVE : AccessStatus.valueOf(value.trim().toUpperCase());
    }
}
