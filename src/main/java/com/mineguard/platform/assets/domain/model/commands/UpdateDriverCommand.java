package com.mineguard.platform.assets.domain.model.commands;

public record UpdateDriverCommand(Long id, String username, String password, String email, String fullName,
                                  String licenseNumber, String workShift) {
}
