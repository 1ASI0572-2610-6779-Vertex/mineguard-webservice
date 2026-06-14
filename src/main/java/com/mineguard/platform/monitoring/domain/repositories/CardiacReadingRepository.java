package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.CardiacReading;

import java.util.List;

public interface CardiacReadingRepository {
    CardiacReading save(CardiacReading reading);
    List<CardiacReading> findAll();
}
