package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Read-model backing the cardiac readings monitoring widget. */
@Getter
public class CardiacReading extends AbstractDomainAggregateRoot<CardiacReading> {
    @Setter private Long id;
    @Setter private String driverName;
    @Setter private String vehicleCode;
    @Setter private int heartRate;
    @Setter private String status;

    public CardiacReading() {
    }

    public CardiacReading(String driverName, String vehicleCode, int heartRate, String status) {
        this.driverName = driverName;
        this.vehicleCode = vehicleCode;
        this.heartRate = heartRate;
        this.status = status;
    }
}
