package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.monitoring.application.domain.model.valueobjects.CardiacStatus;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Heart-rate reading aggregate root ingested from a smart-band edge device.
 *
 * <p>Cloud counterpart of the {@code smart-band-edge-service} HealthRecord:
 * carries the {@code deviceId}, the {@code bpm} value and the UTC timestamp.</p>
 */
@Getter
public class HeartRateReading extends AbstractDomainAggregateRoot<HeartRateReading> {
    @Setter private Long id;
    @Setter private String deviceId;
    @Setter private Long driverId;
    @Setter private double bpm;
    @Setter private String createdAt;

    public HeartRateReading() {
    }

    public HeartRateReading(String deviceId, Long driverId, double bpm, String createdAt) {
        this.deviceId = deviceId;
        this.driverId = driverId;
        this.bpm = bpm;
        this.createdAt = createdAt;
    }

    /** Derived cardiac status for this reading. */
    public CardiacStatus cardiacStatus() {
        return CardiacStatus.fromBpm(bpm);
    }

    /** Whether this reading should raise a fatigue alert. */
    public boolean indicatesFatigue() {
        return cardiacStatus() != CardiacStatus.NORMAL;
    }
}
