package com.mineguard.platform.monitoring.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.monitoring.domain.model.aggregates.FleetSummary;
import com.mineguard.platform.monitoring.domain.repositories.FleetSummaryRepository;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.assemblers.FleetSummaryPersistenceAssembler;
import com.mineguard.platform.monitoring.infrastructure.persistence.jpa.repositories.FleetSummaryPersistenceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FleetSummaryRepositoryImpl implements FleetSummaryRepository {
    private final FleetSummaryPersistenceRepository repository;

    public FleetSummaryRepositoryImpl(FleetSummaryPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public FleetSummary save(FleetSummary summary) {
        return FleetSummaryPersistenceAssembler.toDomain(repository.save(FleetSummaryPersistenceAssembler.toEntity(summary)));
    }

    @Override
    public Optional<FleetSummary> findFirst() {
        return repository.findAll(PageRequest.of(0, 1)).stream().findFirst()
                .map(FleetSummaryPersistenceAssembler::toDomain);
    }
}
