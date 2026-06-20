package com.mineguard.platform.iam.interfaces.rest.resources;

/** Create-supervisor request body. */
public record CreateSupervisorResource(String username, String password, String email, String fullName,
                                       Long idCompany, String corporateId) {
}
