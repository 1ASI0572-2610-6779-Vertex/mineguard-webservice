package com.mineguard.platform.iam.interfaces.rest.resources;

/** Mobile authentication response: {workerId, fullName, role, token, driverId}. */
public record MobileAuthenticatedUserResource(String workerId, String fullName, String role, String token, Long driverId) {
}
