package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;

import java.util.List;
import java.util.Optional;

/** Domain repository port for {@link Supervisor} aggregates. */
public interface SupervisorRepository {
    Supervisor save(Supervisor supervisor);
    Optional<Supervisor> findById(Long id);
    boolean existsByCorporateId(String corporateId);
    List<Supervisor> findAll();
}
