package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.FleetSummaryQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.domain.model.queries.GetFleetSummaryQuery;
import com.mineguard.platform.monitoring.domain.repositories.FleetSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FleetSummaryQueryServiceImpl implements FleetSummaryQueryService {
    private final FleetSummaryRepository repository;

    public FleetSummaryQueryServiceImpl(FleetSummaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<FleetSummary> handle(GetFleetSummaryQuery query) {
        return repository.findFirst();
    }
}
