package com.mineguard.platform.analytics.interfaces.rest.resources;
public record DashboardRiskDriverResource(Long id, Long driverId, String driverName, String vehicleType, double riskScore) {}
