package com.mineguard.platform.monitoring.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.LiveMapVehicle;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllLiveMapVehiclesQuery;

import java.util.List;

public interface LiveMapVehicleQueryService {
    List<LiveMapVehicle> handle(GetAllLiveMapVehiclesQuery query);
}
