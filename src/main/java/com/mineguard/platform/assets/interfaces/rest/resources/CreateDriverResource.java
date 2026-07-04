package com.mineguard.platform.assets.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Create-driver request body. Deliberately has no {@code username}/{@code password} fields —
 * both are always generated server-side on creation (see {@code DriverCommandServiceImpl}), so
 * accepting them would silently discard client input. {@code ignoreUnknown = false} makes Jackson
 * reject the payload with 400 if the client sends either (or any other unrecognized field).
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record CreateDriverResource(
        @Email String email,
        @NotBlank String fullName,
        @NotBlank String licenseNumber,
        String workShift) {
}
