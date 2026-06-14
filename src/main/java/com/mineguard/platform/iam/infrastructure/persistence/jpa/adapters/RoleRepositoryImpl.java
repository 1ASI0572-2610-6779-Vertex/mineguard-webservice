package com.mineguard.platform.iam.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;
import com.mineguard.platform.iam.domain.repositories.RoleRepository;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers.RolePersistenceAssembler;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final RolePersistenceRepository repository;

    public RoleRepositoryImpl(RolePersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role save(Role role) {
        var saved = repository.save(RolePersistenceAssembler.toEntity(role));
        return RolePersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return repository.findByName(name).map(RolePersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByName(Roles name) {
        return repository.existsByName(name);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll().stream().map(RolePersistenceAssembler::toDomain).toList();
    }
}
