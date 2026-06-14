package com.mineguard.platform.iam.domain.model.commands;

/**
 * Command to create a supervisor account in the administration directory.
 */
public record CreateSupervisorCommand(String fullName, String corporateId, String email) {
}
