package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.UpdateSupervisorCommand;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.iam.interfaces.rest.resources.UpdateSupervisorResource;

public final class UpdateSupervisorCommandFromResourceAssembler {
    private UpdateSupervisorCommandFromResourceAssembler() {
    }

    public static UpdateSupervisorCommand toCommandFromResource(Long id, UpdateSupervisorResource resource) {
        AccessStatus status = resource.accessStatus() == null ? null
                : AccessStatus.fromSerialized(resource.accessStatus());
        return new UpdateSupervisorCommand(id, resource.fullName(), resource.corporateId(), resource.email(), status);
    }
}
