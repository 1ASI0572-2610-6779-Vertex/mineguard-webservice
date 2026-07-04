package com.mineguard.platform.assets.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/** Web create-vehicle request body. {@code status} defaults to OPERATIONAL when omitted. */
public record CreateVehicleResource(
        @NotBlank String code,
        @NotBlank String model,
        @NotBlank String category,
        String status,
        String assignedDriverName,
        String shiftLabel) {
}
