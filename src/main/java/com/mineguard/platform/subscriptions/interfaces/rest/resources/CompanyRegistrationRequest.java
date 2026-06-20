package com.mineguard.platform.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyRegistrationRequest(
        @NotBlank String companyName,
        @NotBlank String adminFullName,
        @NotBlank @Email String adminEmail) {
}
