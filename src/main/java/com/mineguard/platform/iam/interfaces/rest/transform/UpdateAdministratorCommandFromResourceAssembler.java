package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.UpdateAdministratorCommand;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
import com.mineguard.platform.iam.interfaces.rest.resources.UpdateAdministratorResource;

public final class UpdateAdministratorCommandFromResourceAssembler {
    private UpdateAdministratorCommandFromResourceAssembler() {}

    public static UpdateAdministratorCommand toCommandFromResource(Long id, UpdateAdministratorResource resource) {
        AccessStatus status = resource.accessStatus() == null ? null : AccessStatus.fromSerialized(resource.accessStatus());
        return new UpdateAdministratorCommand(id, resource.fullName(), resource.email(), status);
    }
}