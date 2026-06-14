package com.mineguard.platform.iam.interfaces.rest.resources;

/** Mobile authentication response: {workerId, fullName, role, token}. */
public record MobileAuthenticatedUserResource(String workerId, String fullName, String role, String token) {
}
