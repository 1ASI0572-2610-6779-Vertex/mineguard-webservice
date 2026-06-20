package com.mineguard.platform.monitoring.domain.model.commands;

/**
 * Command to update a vehicle's GPS coordinates on the live map.
 *
 * @param vehicleId database id of the vehicle whose position must be updated
 * @param lat       GPS latitude received from the edge sensor
 * @param lng       GPS longitude received from the edge sensor
 */
public record UpdateVehicleLocationCommand(Long vehicleId, double lat, double lng) {}
