package com.mineguard.platform.iam.domain.model.commands;
import com.mineguard.platform.iam.domain.model.valueobjects.AccessStatus;
public record UpdateAdministratorCommand(Long id, String fullName, String email, AccessStatus accessStatus) {}