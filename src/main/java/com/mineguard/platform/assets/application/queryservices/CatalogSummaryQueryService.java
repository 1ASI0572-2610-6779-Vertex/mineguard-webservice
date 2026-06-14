package com.mineguard.platform.assets.application.queryservices;

import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.domain.model.queries.GetCatalogSummaryQuery;

import java.util.Optional;

public interface CatalogSummaryQueryService {
    Optional<CatalogSummary> handle(GetCatalogSummaryQuery query);
}
