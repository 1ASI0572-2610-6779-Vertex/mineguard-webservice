package com.mineguard.platform.iam.interfaces.rest.resources;

/** Web authentication response: {id, username, token, role, requiresPasswordChange}. */
public record AuthenticatedUserResource(Long id, String username, String token, String role,
                                        boolean requiresPasswordChange) {
}
