package com.mineguard.platform.iam.interfaces.rest.resources;

/** Web authentication response: {id, username, token, role}. */
public record AuthenticatedUserResource(Long id, String username, String token, String role) {
}
