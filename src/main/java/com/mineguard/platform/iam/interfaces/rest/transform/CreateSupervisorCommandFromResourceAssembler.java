package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.CreateSupervisorCommand;
import com.mineguard.platform.iam.interfaces.rest.resources.CreateSupervisorResource;

public final class CreateSupervisorCommandFromResourceAssembler {
    private CreateSupervisorCommandFromResourceAssembler() {
    }

    /** The company is always the caller's own tenant — never taken from the request body. */
    public static CreateSupervisorCommand toCommandFromResource(CreateSupervisorResource resource, Long companyId) {
        return new CreateSupervisorCommand(resource.email(),
                resource.fullName(), companyId, resource.corporateId());
    }
}
