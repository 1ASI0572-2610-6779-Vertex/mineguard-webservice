package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Read-model backing the fleet summary widget. */
@Getter
public class FleetSummary extends AbstractDomainAggregateRoot<FleetSummary> {
    @Setter private Long id;
    @Setter private int operational;
    @Setter private int maintenance;
    @Setter private int alert;
    @Setter private int total;
    @Setter private int operationalPercent;

    public FleetSummary() {
    }

    public FleetSummary(int operational, int maintenance, int alert, int total, int operationalPercent) {
        this.operational = operational;
        this.maintenance = maintenance;
        this.alert = alert;
        this.total = total;
        this.operationalPercent = operationalPercent;
    }
}
