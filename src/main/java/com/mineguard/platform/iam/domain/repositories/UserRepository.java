package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

/** Domain repository port for {@link User} aggregates. */
public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    List<User> findAll();
}
