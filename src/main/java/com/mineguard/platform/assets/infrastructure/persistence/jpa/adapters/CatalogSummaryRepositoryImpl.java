package com.mineguard.platform.assets.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.domain.repositories.CatalogSummaryRepository;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.assemblers.CatalogSummaryPersistenceAssembler;
import com.mineguard.platform.assets.infrastructure.persistence.jpa.repositories.CatalogSummaryPersistenceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CatalogSummaryRepositoryImpl implements CatalogSummaryRepository {
    private final CatalogSummaryPersistenceRepository repository;

    public CatalogSummaryRepositoryImpl(CatalogSummaryPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public CatalogSummary save(CatalogSummary summary) {
        return CatalogSummaryPersistenceAssembler.toDomain(repository.save(CatalogSummaryPersistenceAssembler.toEntity(summary)));
    }

    @Override
    public Optional<CatalogSummary> findFirst() {
        return repository.findAll(PageRequest.of(0, 1)).stream().findFirst()
                .map(CatalogSummaryPersistenceAssembler::toDomain);
    }
}
