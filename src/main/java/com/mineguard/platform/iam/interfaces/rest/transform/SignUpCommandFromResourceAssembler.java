package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.SignUpCommand;
import com.mineguard.platform.iam.domain.model.entities.Role;
import com.mineguard.platform.iam.interfaces.rest.resources.SignUpResource;

import java.util.List;

public final class SignUpCommandFromResourceAssembler {
    private SignUpCommandFromResourceAssembler() {
    }

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        List<Role> roles = resource.roles() == null ? List.of()
                : resource.roles().stream().map(Role::toRoleFromName).toList();
        return new SignUpCommand(resource.username(), resource.password(),
                resource.email(), resource.fullName(), roles);
    }
}
