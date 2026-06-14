package com.mineguard.platform.iam.interfaces.rest.resources;

import java.util.List;

/** Web sign-up request body. Roles are optional (defaults applied when empty). */
public record SignUpResource(String username, String password, String email, String fullName, List<String> roles) {
}
