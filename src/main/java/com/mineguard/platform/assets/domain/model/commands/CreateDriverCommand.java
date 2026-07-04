package com.mineguard.platform.assets.domain.model.commands;

public record CreateDriverCommand(String email, String fullName,
                                  Long companyId, String licenseNumber, String workShift) {
}
