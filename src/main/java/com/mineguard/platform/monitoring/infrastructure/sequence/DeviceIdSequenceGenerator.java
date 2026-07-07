package com.mineguard.platform.monitoring.infrastructure.sequence;

import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.entities.CompanyDeviceSequencePersistenceEntity;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.CompanyDeviceSequencePersistenceRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hands out the next sequential, per-company {@code deviceId} for a sensor.
 *
 * <p>Each company's ids start at 1 and increase monotonically (company A: 1,2,3…; company B: 1,2,3…).
 * The value is produced by atomically bumping a per-company counter row under a pessimistic write
 * lock, which serializes concurrent linkers so two supervisors provisioning devices in parallel can
 * never receive the same id. The counter never decreases, so ids are never reused — a retired
 * device's id stays reserved forever.</p>
 *
 * <p>Runs in its own transaction ({@code REQUIRES_NEW}) so the counter bump commits independently of
 * the surrounding device-linking work: a rolled-back link simply leaves a gap in the sequence, which
 * is acceptable since ids are never reused. Because this is a distinct bean from the calling command
 * service, the transactional proxy is honoured (no self-invocation). The only race the lock does not
 * cover is two transactions creating the very first counter row for a company at the same instant;
 * the losing insert throws {@link org.springframework.dao.DataIntegrityViolationException}, which the
 * caller retries — by then the row exists and the lock fully serializes the rest.</p>
 */
@Component
public class DeviceIdSequenceGenerator {

    private final CompanyDeviceSequencePersistenceRepository repository;

    public DeviceIdSequenceGenerator(CompanyDeviceSequencePersistenceRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long next(Long companyId) {
        var sequence = repository.findByCompanyId(companyId)
                .orElseGet(() -> {
                    var created = new CompanyDeviceSequencePersistenceEntity();
                    created.setCompanyId(companyId);
                    created.setLastValue(0L);
                    return created;
                });
        long value = sequence.getLastValue() + 1;
        sequence.setLastValue(value);
        repository.saveAndFlush(sequence);
        return value;
    }
}
