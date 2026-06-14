package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

/** Domain repository port for {@link Role} entities. */
public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findByName(Roles name);
    boolean existsByName(Roles name);
    List<Role> findAll();
}
