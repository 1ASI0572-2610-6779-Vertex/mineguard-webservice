package com.mineguard.platform.assets.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "catalog_summaries")
@Getter
@Setter
@NoArgsConstructor
public class CatalogSummaryPersistenceEntity extends AuditableAbstractPersistenceEntity {
    private int driversTotal;
    private int driversInactive;
    private int vehiclesTotal;
    private int vehiclesMaintenance;
    private int supervisorsTotal;
    private int supervisorsLocked;
}
