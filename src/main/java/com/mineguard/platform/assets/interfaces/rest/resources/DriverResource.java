package com.mineguard.platform.assets.interfaces.rest.resources;

/** Web drivers-directory resource (shiftStatus serialized lowercase). */
public record DriverResource(Long id, String fullName, String operatorId, String license, String specialty,
                             String shiftStatus, String lastAccess) {
}
