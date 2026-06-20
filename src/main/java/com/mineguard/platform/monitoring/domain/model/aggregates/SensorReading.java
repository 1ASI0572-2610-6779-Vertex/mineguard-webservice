package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Sensor reading aggregate root (telemetry sample). */
@Getter
public class SensorReading extends AbstractDomainAggregateRoot<SensorReading> {
    @Setter private Long id;
    @Setter private Long sensorId;
    @Setter private String readingType;
    @Setter private double value;
    @Setter private String timestamp;

    public SensorReading() {
    }

    public SensorReading(Long sensorId, String readingType, double value, String timestamp) {
        this.sensorId = sensorId;
        this.readingType = readingType;
        this.value = value;
        this.timestamp = timestamp;
    }
}
