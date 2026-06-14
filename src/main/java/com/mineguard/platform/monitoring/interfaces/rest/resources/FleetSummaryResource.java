package com.mineguard.platform.monitoring.interfaces.rest.resources;

public record FleetSummaryResource(Long id, int operational, int maintenance, int alert, int total,
                                   int operationalPercent) {
}
