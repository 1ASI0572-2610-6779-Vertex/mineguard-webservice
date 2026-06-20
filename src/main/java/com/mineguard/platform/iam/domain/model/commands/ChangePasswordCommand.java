package com.mineguard.platform.iam.domain.model.commands;

public record ChangePasswordCommand(Long userId, String newPassword) {
}
