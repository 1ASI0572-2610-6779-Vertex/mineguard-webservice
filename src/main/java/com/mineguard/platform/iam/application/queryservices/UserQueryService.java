package com.mineguard.platform.iam.application.queryservices;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.mineguard.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.mineguard.platform.iam.domain.model.queries.GetUserByUsernameQuery;

import java.util.List;
import java.util.Optional;

/** Query service port for users. */
public interface UserQueryService {
    List<User> handle(GetAllUsersQuery query);
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByUsernameQuery query);
}
