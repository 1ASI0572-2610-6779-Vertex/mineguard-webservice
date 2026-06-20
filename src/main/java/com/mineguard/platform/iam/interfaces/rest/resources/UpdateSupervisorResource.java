package com.mineguard.platform.iam.interfaces.rest.resources;

/** Update-supervisor request body (accessStatus serialized lowercase). */
public record UpdateSupervisorResource(String username, String password, String fullName, String corporateId,
                                       String email, Long idCompany, String accessStatus) {
}
