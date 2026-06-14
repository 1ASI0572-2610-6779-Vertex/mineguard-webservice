package com.mineguard.platform.iam.domain.model.commands;

/**
 * Command to authenticate a field operator from the mobile application using
 * their worker id (mapped to the username) and password.
 */
public record MobileSignInCommand(String workerId, String password) {
}
