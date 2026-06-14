package com.mineguard.platform.monitoring.domain.model.valueobjects;

/** Cardiac status derived from a heart-rate reading. */
public enum CardiacStatus {
    NORMAL, WARNING, CRITICAL;

    public String toSerialized() {
        return name().toLowerCase();
    }

    /** Derives a status from a beats-per-minute value (fatigue/stress heuristic). */
    public static CardiacStatus fromBpm(double bpm) {
        if (bpm >= 120 || bpm < 45) return CRITICAL;
        if (bpm >= 100 || bpm < 55) return WARNING;
        return NORMAL;
    }
}
