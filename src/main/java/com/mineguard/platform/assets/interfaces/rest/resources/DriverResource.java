package com.mineguard.platform.assets.interfaces.rest.resources;

/**
 * Web drivers-directory resource (shiftStatus serialized lowercase).
 * {@code riskScore} is only populated when the collection is requested with
 * {@code ?sort=-riskScore} — {@code null} otherwise.
 */
public record DriverResource(Long id, String fullName, String operatorId, String license, String specialty,
                             String shiftStatus, String lastAccess, Double riskScore) {
}
