package com.mineguard.platform.assets.interfaces.rest.resources;

/**
 * Web partial-update request body for a Driving Session (PATCH semantics). {@code status} is
 * the only patchable field today — setting it to {@code COMPLETED} or {@code CANCELLED} performs
 * the check-out.
 */
public record UpdateDrivingSessionResource(String status) {
}
