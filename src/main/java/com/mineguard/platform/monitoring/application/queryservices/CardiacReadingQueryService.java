package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;
import com.mineguard.platform.monitoring.domain.model.queries.GetCardiacReadingQuery;

import java.util.Optional;

public interface CardiacReadingQueryService {
    /** Returns the latest reading for the session, or empty if none exists / ownership fails. */
    Optional<CardiacReading> handle(GetCardiacReadingQuery query);
}
