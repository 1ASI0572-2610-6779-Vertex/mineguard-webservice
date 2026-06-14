package com.mineguard.platform.iam.interfaces.rest.resources;

/** Update-supervisor request body (accessStatus serialized lowercase). */
public record UpdateSupervisorResource(String fullName, String corporateId, String email, String accessStatus) {
}
