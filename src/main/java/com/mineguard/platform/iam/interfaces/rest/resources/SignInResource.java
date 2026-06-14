package com.mineguard.platform.iam.interfaces.rest.resources;

/** Web sign-in request body. */
public record SignInResource(String username, String password) {
}
