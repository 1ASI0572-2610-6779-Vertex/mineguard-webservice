package com.mineguard.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordResource(
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String newPassword) {
}
