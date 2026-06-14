package com.mineguard.platform.shared.interfaces.rest.resources;

/**
 * Simple message response resource used for acknowledgement-style endpoints.
 */
public record MessageResource(String message) {
}
