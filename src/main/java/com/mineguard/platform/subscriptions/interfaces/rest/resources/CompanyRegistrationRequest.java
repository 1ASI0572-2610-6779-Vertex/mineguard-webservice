package com.mineguard.platform.subscriptions.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyRegistrationRequest(
        @NotBlank String companyName,
        @NotBlank String adminFullName,
        @NotBlank @Email String adminEmail,
        /** Optional descriptive tier: STARTER, STANDARD, ENTERPRISE. Defaults to STANDARD when omitted. */
        String subscriptionPlan) {
}
