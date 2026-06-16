package com.mineguard.platform.iam.interfaces.rest.resources;

public record AdministratorResource(Long id, String fullName, String email, String accessStatus, Long userId) {}