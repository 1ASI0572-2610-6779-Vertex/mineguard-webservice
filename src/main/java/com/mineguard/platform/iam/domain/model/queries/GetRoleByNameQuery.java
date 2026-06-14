package com.mineguard.platform.iam.domain.model.queries;

import com.mineguard.platform.iam.domain.model.valueobjects.Roles;

/** Query to retrieve a role by its name. */
public record GetRoleByNameQuery(Roles name) {
}
