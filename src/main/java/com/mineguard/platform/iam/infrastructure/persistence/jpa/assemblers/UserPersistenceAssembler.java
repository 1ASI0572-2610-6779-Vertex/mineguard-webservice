package com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

import java.util.stream.Collectors;

/** Translates between {@link User} aggregates and {@link UserPersistenceEntity}. */
public final class UserPersistenceAssembler {
    private UserPersistenceAssembler() {
    }

    public static User toDomain(UserPersistenceEntity entity) {
        if (entity == null) return null;
        var user = new User(entity.getUsername(), entity.getPassword());
        user.setId(entity.getId());
        user.setEmail(entity.getEmail());
        user.setFullName(entity.getFullName());
        user.setCompanyId(entity.getCompanyId());
        user.setRoles(entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomain)
                .collect(Collectors.toSet()));
        user.setRequiresPasswordChange(entity.isRequiresPasswordChange());
        return user;
    }

    public static UserPersistenceEntity toEntity(User user) {
        var entity = new UserPersistenceEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setEmail(user.getEmail());
        entity.setFullName(user.getFullName());
        entity.setCompanyId(user.getCompanyId());
        entity.setRequiresPasswordChange(user.isRequiresPasswordChange());
        entity.setRoles(user.getRoles().stream()
                .map(RolePersistenceAssembler::toEntity)
                .collect(Collectors.toSet()));
        return entity;
    }
}
