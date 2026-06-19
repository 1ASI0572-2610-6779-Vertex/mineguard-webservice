package com.mineguard.platform.assets.domain.model.commands;

public record CreateDriverCommand(String username, String password, String email, String fullName,
                                  Long idCompany, String licenseNumber, String workShift) {
}
