package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import java.util.List;
import java.util.Optional;

public interface AdministratorRepository {
    Administrator save(Administrator administrator);
    Optional<Administrator> findById(Long id);
    boolean existsByEmail(String email);
    List<Administrator> findAll();
}