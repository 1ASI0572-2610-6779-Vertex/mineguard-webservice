package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllCardiacReadingsQuery;

import java.util.List;

public interface CardiacReadingQueryService {
    List<CardiacReading> handle(GetAllCardiacReadingsQuery query);
}
