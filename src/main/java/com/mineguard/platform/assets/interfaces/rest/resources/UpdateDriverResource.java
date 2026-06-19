package com.mineguard.platform.assets.interfaces.rest.resources;

public record UpdateDriverResource(String username, String password, String email, String fullName,
                                   Long idCompany, String licenseNumber, String workShift) {
}
