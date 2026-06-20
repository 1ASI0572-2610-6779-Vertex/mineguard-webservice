package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.domain.model.queries.GetFleetSummaryQuery;

import java.util.Optional;

public interface FleetSummaryQueryService {
    Optional<FleetSummary> handle(GetFleetSummaryQuery query);
}
