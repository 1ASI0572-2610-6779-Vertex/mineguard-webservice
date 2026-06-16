package com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.SupervisorPersistenceEntity;

/** Translates between {@link Supervisor} aggregates and {@link SupervisorPersistenceEntity}. */
public final class SupervisorPersistenceAssembler {
    private SupervisorPersistenceAssembler() {}

    public static Supervisor toDomain(SupervisorPersistenceEntity entity) {
        if (entity == null) return null;
        var supervisor = new Supervisor(entity.getFullName(), entity.getCorporateId(),
                entity.getEmail(), entity.getAccessStatus(), entity.getUserId());
        supervisor.setId(entity.getId());
        return supervisor;
    }

    public static SupervisorPersistenceEntity toEntity(Supervisor supervisor) {
        var entity = new SupervisorPersistenceEntity();
        entity.setId(supervisor.getId());
        entity.setFullName(supervisor.getFullName());
        entity.setCorporateId(supervisor.getCorporateId());
        entity.setEmail(supervisor.getEmail());
        entity.setAccessStatus(supervisor.getAccessStatus());
        entity.setUserId(supervisor.getUserId());
        return entity;
    }
}