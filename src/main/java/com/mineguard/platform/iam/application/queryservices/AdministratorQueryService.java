package com.mineguard.platform.iam.application.queryservices;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.domain.model.queries.GetAllAdministratorsQuery;
import java.util.List;

public interface AdministratorQueryService {
    List<Administrator> handle(GetAllAdministratorsQuery query);
}