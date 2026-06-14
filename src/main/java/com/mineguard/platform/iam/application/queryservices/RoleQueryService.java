package com.mineguard.platform.iam.application.queryservices;

import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.mineguard.platform.iam.domain.model.queries.GetRoleByNameQuery;

import java.util.List;
import java.util.Optional;

/** Query service port for roles. */
public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
    Optional<Role> handle(GetRoleByNameQuery query);
}
