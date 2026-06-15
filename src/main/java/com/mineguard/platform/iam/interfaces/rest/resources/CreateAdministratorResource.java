package com.mineguard.platform.iam.interfaces.rest.resources;

public record CreateAdministratorResource(String fullName, String email, Long userId) {}