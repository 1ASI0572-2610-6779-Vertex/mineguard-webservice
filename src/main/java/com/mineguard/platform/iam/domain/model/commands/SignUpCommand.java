package com.mineguard.platform.iam.domain.model.commands;

import com.mineguard.platform.iam.domain.model.entities.Role;

import java.util.List;

/**
 * Command to register a new user with credentials, profile data and roles.
 */
public record SignUpCommand(String username, String password, String email, String fullName, List<Role> roles) {
}
