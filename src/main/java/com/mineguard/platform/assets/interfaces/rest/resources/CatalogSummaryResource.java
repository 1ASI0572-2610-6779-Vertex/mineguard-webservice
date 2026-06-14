package com.mineguard.platform.assets.interfaces.rest.resources;

/** Assets catalog summary resource. */
public record CatalogSummaryResource(Long id, int driversTotal, int driversInactive, int vehiclesTotal,
                                     int vehiclesMaintenance, int supervisorsTotal, int supervisorsLocked) {
}
