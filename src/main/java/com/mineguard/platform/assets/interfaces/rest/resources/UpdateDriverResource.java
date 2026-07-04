package com.mineguard.platform.assets.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/** Every field is optional — {@code null} leaves the current value unchanged (see DriverCommandServiceImpl). */
public record UpdateDriverResource(
        String username,
        @Size(min = 6) String password,
        @Email String email,
        String fullName,
        String licenseNumber,
        String workShift) {
}
