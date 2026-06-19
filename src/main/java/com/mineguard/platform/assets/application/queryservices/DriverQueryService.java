package com.mineguard.platform.assets.application.queryservices;

import com.mineguard.platform.assets.domain.model.aggregates.Driver;
import com.mineguard.platform.assets.domain.model.queries.GetAllDriversQuery;
import com.mineguard.platform.assets.domain.model.queries.GetDriverByIdQuery;

import java.util.List;
import java.util.Optional;

public interface DriverQueryService {
    List<Driver> handle(GetAllDriversQuery query);
    Optional<Driver> handle(GetDriverByIdQuery query);
}
