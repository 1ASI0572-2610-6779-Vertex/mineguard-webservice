package com.mineguard.platform.monitoring.domain.model.aggregates;

import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/** Incident aggregate root, derived from an alert. */
@Getter
public class Incident extends AbstractDomainAggregateRoot<Incident> {
    @Setter private Long id;
    @Setter private Long alertId;
    @Setter private String description;
    @Setter private String incidentDate;
    @Setter private String status;
    @Setter private String severity;

    public Incident() {
    }

    public Incident(Long alertId, String description, String incidentDate, String status, String severity) {
        this.alertId = alertId;
        this.description = description;
        this.incidentDate = incidentDate;
        this.status = status;
        this.severity = severity;
    }
}
