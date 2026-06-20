package com.mineguard.platform.iam.application.commandservices;

import com.mineguard.platform.iam.domain.model.commands.SeedRolesCommand;

/** Command service port for role seeding. */
public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}
