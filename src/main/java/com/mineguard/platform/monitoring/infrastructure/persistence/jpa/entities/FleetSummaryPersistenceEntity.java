package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fleet_summaries")
@Getter
@Setter
@NoArgsConstructor
public class FleetSummaryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    private int operational;
    private int maintenance;
    private int alert;
    private int total;
    private int operationalPercent;
}
