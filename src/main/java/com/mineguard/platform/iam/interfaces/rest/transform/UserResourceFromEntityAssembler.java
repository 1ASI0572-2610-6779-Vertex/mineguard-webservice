package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.interfaces.rest.resources.UserResource;

public final class UserResourceFromEntityAssembler {
    private UserResourceFromEntityAssembler() {
    }

    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(user.getId(), user.getUsername());
    }
}
