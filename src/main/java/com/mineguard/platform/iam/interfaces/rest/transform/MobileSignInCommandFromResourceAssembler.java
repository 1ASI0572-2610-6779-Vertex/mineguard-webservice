package com.mineguard.platform.iam.interfaces.rest.transform;

import com.mineguard.platform.iam.domain.model.commands.MobileSignInCommand;
import com.mineguard.platform.iam.interfaces.rest.resources.MobileSignInResource;

public final class MobileSignInCommandFromResourceAssembler {
    private MobileSignInCommandFromResourceAssembler() {
    }

    public static MobileSignInCommand toCommandFromResource(MobileSignInResource resource) {
        return new MobileSignInCommand(resource.workerId(), resource.password());
    }
}
