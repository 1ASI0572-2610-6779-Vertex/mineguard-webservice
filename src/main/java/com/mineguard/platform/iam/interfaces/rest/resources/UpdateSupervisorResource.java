package com.mineguard.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Update-supervisor request body (PATCH semantics; accessStatus serialized lowercase).
 * Every field is optional — {@code null} leaves the current value unchanged
 * (see {@code Supervisor.updateInformation} / {@code SupervisorCommandServiceImpl}).
 */
public record UpdateSupervisorResource(
        String username,
        @Size(min = 6) String password,
        String fullName,
        String corporateId,
        @Email String email,
        String accessStatus) {
}
