package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.interfaces.rest.resources.MobileAuthenticatedUserResource;

public final class MobileAuthenticatedUserResourceFromEntityAssembler {
    private MobileAuthenticatedUserResourceFromEntityAssembler() {
    }

    public static MobileAuthenticatedUserResource toResourceFromEntity(User user, String token, Long driverId) {
        var workerId = user.getUsername();
        var fullName = user.getFullName() != null ? user.getFullName() : user.getUsername();
        return new MobileAuthenticatedUserResource(workerId, fullName, RoleLabel.mobile(user), token, driverId);
    }
}
