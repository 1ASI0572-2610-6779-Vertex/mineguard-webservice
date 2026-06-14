package com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;

/** Translates between {@link Role} domain entities and {@link RolePersistenceEntity}. */
public final class RolePersistenceAssembler {
    private RolePersistenceAssembler() {
    }

    public static Role toDomain(RolePersistenceEntity entity) {
        if (entity == null) return null;
        var role = new Role(entity.getName());
        role.setId(entity.getId());
        return role;
    }

    public static RolePersistenceEntity toEntity(Role role) {
        var entity = new RolePersistenceEntity(role.getName());
        entity.setId(role.getId());
        return entity;
    }
}
