package com.mineguard.platform.monitoring.application.application.queryservices;

import com.mineguard.platform.monitoring.domain.model.aggregates.Alert;
import com.mineguard.platform.monitoring.domain.model.queries.GetAlertByIdQuery;
import com.mineguard.platform.monitoring.domain.model.queries.GetAllAlertsQuery;

import java.util.List;
import java.util.Optional;

public interface AlertQueryService {
    List<Alert> handle(GetAllAlertsQuery query);
    Optional<Alert> handle(GetAlertByIdQuery query);
}
