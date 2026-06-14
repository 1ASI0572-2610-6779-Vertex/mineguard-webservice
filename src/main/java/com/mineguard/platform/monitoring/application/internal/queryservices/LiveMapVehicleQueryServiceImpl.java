package com.mineguard.platform.monitoring.application.internal.queryservices;

import com.mineguard.platform.monitoring.application.queryservices.LiveMapVehicleQueryService;
import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllLiveMapVehiclesQuery;
import com.mineguard.platform.monitoring.domain.repositories.LiveMapVehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveMapVehicleQueryServiceImpl implements LiveMapVehicleQueryService {
    private final LiveMapVehicleRepository repository;

    public LiveMapVehicleQueryServiceImpl(LiveMapVehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LiveMapVehicle> handle(GetAllLiveMapVehiclesQuery query) {
        return repository.findAll();
    }
}
