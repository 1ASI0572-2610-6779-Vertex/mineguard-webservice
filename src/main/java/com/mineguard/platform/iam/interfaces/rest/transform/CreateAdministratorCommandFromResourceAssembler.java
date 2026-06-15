package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.CreateAdministratorCommand;
import com.mineguard.platform.iam.interfaces.rest.resources.CreateAdministratorResource;

public final class CreateAdministratorCommandFromResourceAssembler {
    private CreateAdministratorCommandFromResourceAssembler() {}

    public static CreateAdministratorCommand toCommandFromResource(CreateAdministratorResource resource) {
        return new CreateAdministratorCommand(resource.fullName(), resource.email(), resource.userId());
    }
}