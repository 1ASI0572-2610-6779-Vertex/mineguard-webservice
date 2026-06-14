package com.mineguard.platform.assets.application.internal.queryservices;

import com.mineguard.platform.assets.application.queryservices.CatalogSummaryQueryService;
import com.mineguard.platform.assets.domain.model.aggregates.CatalogSummary;
import com.mineguard.platform.assets.domain.model.queries.GetCatalogSummaryQuery;
import com.mineguard.platform.assets.domain.repositories.CatalogSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatalogSummaryQueryServiceImpl implements CatalogSummaryQueryService {
    private final CatalogSummaryRepository catalogSummaryRepository;

    public CatalogSummaryQueryServiceImpl(CatalogSummaryRepository catalogSummaryRepository) {
        this.catalogSummaryRepository = catalogSummaryRepository;
    }

    @Override
    public Optional<CatalogSummary> handle(GetCatalogSummaryQuery query) {
        return catalogSummaryRepository.findFirst();
    }
}
