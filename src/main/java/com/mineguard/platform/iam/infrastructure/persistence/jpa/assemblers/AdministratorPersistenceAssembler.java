package com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.AdministratorPersistenceEntity;

public final class AdministratorPersistenceAssembler {
    private AdministratorPersistenceAssembler() {}

    public static Administrator toDomain(AdministratorPersistenceEntity entity) {
        if (entity == null) return null;
        var administrator = new Administrator(entity.getFullName(), entity.getEmail(),
                entity.getAccessStatus(), entity.getUserId());
        administrator.setId(entity.getId());
        return administrator;
    }

    public static AdministratorPersistenceEntity toEntity(Administrator administrator) {
        var entity = new AdministratorPersistenceEntity();
        entity.setId(administrator.getId());
        entity.setFullName(administrator.getFullName());
        entity.setEmail(administrator.getEmail());
        entity.setAccessStatus(administrator.getAccessStatus());
        entity.setUserId(administrator.getUserId());
        return entity;
    }
}