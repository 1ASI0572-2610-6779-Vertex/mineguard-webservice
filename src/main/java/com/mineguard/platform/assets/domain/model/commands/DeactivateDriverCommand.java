package com.mineguard.platform.assets.domain.model.commands;

/**
 * Command to deactivate a driver — a soft delete that marks the driver INACTIVE without removing the
 * row, preserving historical reports and driving sessions. The driver is then excluded from the
 * directory by default.
 *
 * @param id the driver to deactivate (must belong to the caller's company)
 */
public record DeactivateDriverCommand(Long id) {
}
