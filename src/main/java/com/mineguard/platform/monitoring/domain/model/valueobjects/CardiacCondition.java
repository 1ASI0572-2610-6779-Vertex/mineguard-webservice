package com.mineguard.platform.monitoring.domain.model.valueobjects;

import java.util.Optional;

/**
 * Classification of a heart-rate sample into the alert-worthy conditions the platform recognises.
 *
 * <p>Pure domain logic: no persistence, no HTTP. {@link #classify(double)} is the single source of
 * truth for which BPM values raise an alert and at which severity.</p>
 *
 * <p><b>Known blind spot.</b> {@link #CARDIAC_ARREST} nominally covers {@code 0 < bpm <= 40}, but a
 * real arrest drives the reading toward zero, and the platform contract defines {@code bpm == 0} as
 * "no sample this cycle" — indistinguishable from a smart-band that lost skin contact. The edge
 * gateway compounds this by normalising {@code 0 < bpm < 30} to {@code 0.0} as sensor noise. Until
 * the device reports contact state separately, the effective detection window is roughly 35–40 bpm.</p>
 */
public enum CardiacCondition {

    /** Tachycardia — sustained high heart rate under load. */
    HIGH_HEART_RATE("high_heart_rate", AlertPriority.CRITICAL, "High heart rate detected"),

    /** Bradycardia bordering on cardiac arrest. */
    CARDIAC_ARREST("cardiac_arrest", AlertPriority.CRITICAL, "Critically low heart rate detected"),

    /** Elevated but sub-critical — the operator is under strain and should be relieved. */
    FATIGUE_RISK("fatigue_risk", AlertPriority.MEDIUM, "Fatigue risk detected");

    /** At or above this the operator is tachycardic. */
    public static final double TACHYCARDIA_BPM = 140.0;
    /** At or below this (and above zero) the operator is bradycardic. */
    public static final double BRADYCARDIA_BPM = 40.0;
    /** At or above this, but below {@link #TACHYCARDIA_BPM}, the operator is showing strain. */
    public static final double FATIGUE_BPM = 110.0;

    private final String rawType;
    private final AlertPriority priority;
    private final String title;

    CardiacCondition(String rawType, AlertPriority priority, String title) {
        this.rawType = rawType;
        this.priority = priority;
        this.title = title;
    }

    public String rawType() {
        return rawType;
    }

    public AlertPriority priority() {
        return priority;
    }

    public String title() {
        return title;
    }

    /**
     * @param bpm heart rate in beats per minute; {@code 0} means "no reading this cycle"
     * @return the condition this reading breaches, or empty when it is within normal range
     */
    public static Optional<CardiacCondition> classify(double bpm) {
        if (bpm <= 0) return Optional.empty();
        if (bpm >= TACHYCARDIA_BPM) return Optional.of(HIGH_HEART_RATE);
        if (bpm <= BRADYCARDIA_BPM) return Optional.of(CARDIAC_ARREST);
        if (bpm >= FATIGUE_BPM) return Optional.of(FATIGUE_RISK);
        return Optional.empty();
    }
}
