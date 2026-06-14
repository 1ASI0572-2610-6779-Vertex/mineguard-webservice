package com.mineguard.platform.assets.domain.repositories;

import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;

import java.util.Optional;

public interface CatalogSummaryRepository {
    CatalogSummary save(CatalogSummary summary);
    Optional<CatalogSummary> findFirst();
}
