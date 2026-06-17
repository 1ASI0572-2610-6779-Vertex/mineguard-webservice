package com.mineguard.platform.analytics.interfaces.rest.resources;
public record AnalyticsIncidentDistributionResource(Long id, String label, int count, int percent, String className) {}
