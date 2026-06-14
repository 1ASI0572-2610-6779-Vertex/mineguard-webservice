package com.mineguard.platform.iam.domain.model.commands;

import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;

/**
 * Command to update an existing supervisor account.
 */
public record UpdateSupervisorCommand(Long id, String fullName, String corporateId, String email,
                                      AccessStatus accessStatus) {
}
