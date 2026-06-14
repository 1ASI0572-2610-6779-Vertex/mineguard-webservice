package com.mineguard.platform.assets.application.queryservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.queries.GetAllDriversQuery;

import java.util.List;

public interface DriverQueryService {
    List<Driver> handle(GetAllDriversQuery query);
}
