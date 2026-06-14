package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.interfaces.rest.resources.CreateSupervisorResource;

public final class CreateSupervisorCommandFromResourceAssembler {
    private CreateSupervisorCommandFromResourceAssembler() {
    }

    public static CreateSupervisorCommand toCommandFromResource(CreateSupervisorResource resource) {
        return new CreateSupervisorCommand(resource.fullName(), resource.corporateId(), resource.email());
    }
}
