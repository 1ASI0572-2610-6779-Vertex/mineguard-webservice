package com.mineguard.platform.monitoring.domain.repositories;

import com.mineguard.platform.monitoring.domain.model.aggregates.HeartRateReading;

import java.util.List;

public interface HeartRateReadingRepository {
    HeartRateReading save(HeartRateReading reading);
    List<HeartRateReading> findAll();
}
