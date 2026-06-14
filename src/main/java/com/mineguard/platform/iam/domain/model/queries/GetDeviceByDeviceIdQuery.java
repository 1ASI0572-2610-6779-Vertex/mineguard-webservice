package com.mineguard.platform.iam.domain.model.queries;

/** Query to retrieve a device by its natural device identifier. */
public record GetDeviceByDeviceIdQuery(String deviceId) {
}
