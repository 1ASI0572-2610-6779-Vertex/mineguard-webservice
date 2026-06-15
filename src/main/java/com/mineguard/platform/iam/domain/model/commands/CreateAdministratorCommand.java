package com.mineguard.platform.iam.domain.model.commands;

public record CreateAdministratorCommand(String fullName, String email, Long userId) {}