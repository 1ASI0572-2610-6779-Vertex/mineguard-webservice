package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.interfaces.rest.resources.AdministratorResource;

public final class AdministratorResourceFromEntityAssembler {
    private AdministratorResourceFromEntityAssembler() {}

    public static AdministratorResource toResourceFromEntity(Administrator admin) {
        return new AdministratorResource(admin.getId(), admin.getFullName(), admin.getEmail(),
                admin.getAccessStatus().toSerialized(), admin.getUserId());
    }
}