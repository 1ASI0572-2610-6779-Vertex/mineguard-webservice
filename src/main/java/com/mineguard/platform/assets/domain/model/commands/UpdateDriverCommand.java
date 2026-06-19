package com.mineguard.platform.assets.domain.model.commands;

public record UpdateDriverCommand(Long id, String username, String password, String email, String fullName,
                                  Long idCompany, String licenseNumber, String workShift) {
}
