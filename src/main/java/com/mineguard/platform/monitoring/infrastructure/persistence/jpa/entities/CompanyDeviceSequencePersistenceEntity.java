package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities;

import com.mineguard.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Per-company monotonic counter backing the sequential {@code deviceId} assigned to sensors.
 *
 * <p>Each company (tenant) owns exactly one row (enforced by the unique constraint on
 * {@code company_id}); {@code lastValue} holds the highest deviceId handed out so far. The next
 * device gets {@code lastValue + 1}. The counter only ever increments — deviceIds are never reused,
 * so a retired device's id can never be recycled onto a new physical unit (embedded/edge relies on
 * this). Concurrency is handled with a pessimistic write lock on the row (see repository).</p>
 */
@Entity
@Table(name = "company_device_sequences",
        uniqueConstraints = @UniqueConstraint(columnNames = "company_id"))
@Getter
@Setter
@NoArgsConstructor
public class CompanyDeviceSequencePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** Highest deviceId assigned so far for this company. Starts at 0; first device becomes 1. */
    @Column(name = "last_value", nullable = false)
    private long lastValue;
}
