package com.mineguard.platform.iam.interfaces.rest.resources;

/** Create-supervisor request body. */
public record CreateSupervisorResource(String fullName, String corporateId, String email) {
}
