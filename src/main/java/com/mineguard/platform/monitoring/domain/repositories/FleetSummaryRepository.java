package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;

import java.util.Optional;

public interface FleetSummaryRepository {
    FleetSummary save(FleetSummary summary);
    Optional<FleetSummary> findFirst();
}
