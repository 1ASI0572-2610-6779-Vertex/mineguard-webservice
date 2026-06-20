package com.mineguard.platform.iam.application.queryservices;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.model.queries.GetAllSupervisorsQuery;
import com.mineguard.platform.iam.domain.model.queries.GetSupervisorByIdQuery;

import java.util.List;
import java.util.Optional;

/** Query service port for supervisors. */
public interface SupervisorQueryService {
    List<Supervisor> handle(GetAllSupervisorsQuery query);
    Optional<Supervisor> handle(GetSupervisorByIdQuery query);
}
