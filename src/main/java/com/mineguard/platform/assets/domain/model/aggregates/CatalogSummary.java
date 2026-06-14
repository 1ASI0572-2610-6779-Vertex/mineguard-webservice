package com.mineguard.platform.assets.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Read-model aggregate backing the assets catalog summary widget. */
@Getter
public class CatalogSummary extends AbstractDomainAggregateRoot<CatalogSummary> {

    @Setter private Long id;
    @Setter private int driversTotal;
    @Setter private int driversInactive;
    @Setter private int vehiclesTotal;
    @Setter private int vehiclesMaintenance;
    @Setter private int supervisorsTotal;
    @Setter private int supervisorsLocked;

    public CatalogSummary() {
    }

    public CatalogSummary(int driversTotal, int driversInactive, int vehiclesTotal,
                          int vehiclesMaintenance, int supervisorsTotal, int supervisorsLocked) {
        this.driversTotal = driversTotal;
        this.driversInactive = driversInactive;
        this.vehiclesTotal = vehiclesTotal;
        this.vehiclesMaintenance = vehiclesMaintenance;
        this.supervisorsTotal = supervisorsTotal;
        this.supervisorsLocked = supervisorsLocked;
    }
}
