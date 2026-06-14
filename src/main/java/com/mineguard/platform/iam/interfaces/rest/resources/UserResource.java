package com.mineguard.platform.iam.interfaces.rest.resources;

/** Web sign-up response: {id, username}. */
public record UserResource(Long id, String username) {
}
