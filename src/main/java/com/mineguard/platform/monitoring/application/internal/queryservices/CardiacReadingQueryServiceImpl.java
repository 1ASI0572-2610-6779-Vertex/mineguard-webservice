package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.CardiacReadingQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;
import com.mineguard.platform.monitoring.domain.repositories.CardiacReadingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardiacReadingQueryServiceImpl implements CardiacReadingQueryService {
    private final CardiacReadingRepository repository;

    public CardiacReadingQueryServiceImpl(CardiacReadingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CardiacReading> handle(GetAllCardiacReadingsQuery query) {
        return repository.findAll();
    }
}
