package com.mineguard.platform.iam.domain.model.commands;

/**
 * Command to authenticate a user with username and password.
 */
public record SignInCommand(String username, String password) {
}
