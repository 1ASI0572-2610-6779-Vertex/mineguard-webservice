package com.mineguard.platform.subscriptions.domain.model.commands;

/** Registers a new company and provisions its first administrator account. */
public record RegisterCompanyCommand(String companyName, String adminFullName, String adminEmail) {
}
