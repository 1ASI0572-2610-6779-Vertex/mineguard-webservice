package com.mineguard.platform.iam.domain.repositories;

import com.mineguard.platform.iam.domain.model.aggregates.User;

import java.util.List;
import java.util.Optional;

/** Domain repository port for {@link User} aggregates. */
public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    List<User> findAll();
    /** Returns the number of users whose username starts with the given prefix. Used by {@link com.mineguard.platform.shared.domain.utils.UsernameGenerator}. */
    long countByUsernamePrefix(String prefix);
}
