package com.mineguard.platform.analytics.interfaces.rest.resources;
public record DashboardRecentAlertResource(Long id, String alertCode, String severity, String category,
                                           String driverName, String vehicleCode, String vehicleType,
                                           String route, String time, String status) {}
