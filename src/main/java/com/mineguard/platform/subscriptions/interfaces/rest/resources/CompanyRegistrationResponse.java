package com.mineguard.platform.subscriptions.interfaces.rest.resources;

/**
 * Structured response returned by {@code POST /api/v1/companies}. Replaces the former
 * plain-message String response so clients (the landing-page sign-up flow) can consume
 * the generated tenant identifiers programmatically instead of parsing free text.
 */
public record CompanyRegistrationResponse(
        Long companyId,
        String apiKey,
        String adminUsername,
        String message) {
}
