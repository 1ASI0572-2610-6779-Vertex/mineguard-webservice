package com.mineguard.platform.iam.domain.model.aggregates;

import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User aggregate root for the IAM bounded context.
 *
 * <p>Carries the credentials and profile attributes exposed by the platform
 * sign-in contract ({@code id, username, token, role}) plus the e-mail and
 * full name used by directory views.</p>
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    @Setter
    private Long id;
    @Setter
    private String username;
    @Setter
    private String password;
    @Setter
    private String email;
    @Setter
    private String fullName;
    @Setter
    private Set<Role> roles;

    public User() {
        this.roles = new HashSet<>();
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String email, String fullName, List<Role> roles) {
        this(username, password);
        this.email = email;
        this.fullName = fullName;
        addRoles(roles);
    }

    /** Add a role to the user. */
    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    /** Add a list of roles to the user. */
    public User addRoles(List<Role> roles) {
        var validatedRoleSet = Role.validateRoleSet(roles);
        this.roles.addAll(validatedRoleSet);
        return this;
    }

    /** Returns the name of the first assigned role, or the default role name. */
    public String getPrimaryRoleName() {
        return roles.stream().findFirst().map(Role::getStringName).orElse(Role.getDefaultRole().getStringName());
    }
}
