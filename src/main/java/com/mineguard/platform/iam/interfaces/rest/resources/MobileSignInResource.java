package com.mineguard.platform.iam.interfaces.rest.resources;

/** Mobile sign-in request body: {workerId, password}. */
public record MobileSignInResource(String workerId, String password) {
}
