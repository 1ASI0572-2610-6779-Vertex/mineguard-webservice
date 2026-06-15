package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.interfaces.rest.resources.SupervisorResource;

public final class SupervisorResourceFromEntityAssembler {
    private SupervisorResourceFromEntityAssembler() {}

    public static SupervisorResource toResourceFromEntity(Supervisor supervisor) {
        return new SupervisorResource(
                supervisor.getId(),
                supervisor.getFullName(),
                supervisor.getCorporateId(),
                supervisor.getEmail(),
                supervisor.getAccessStatus().toSerialized(),
                supervisor.getUserId()
        );
    }
}