package com.mineguard.platform.iam.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.iam.domain.model.aggregates.User;
import com.mineguard.platform.iam.domain.repositories.UserRepository;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserPersistenceRepository repository;

    public UserRepositoryImpl(UserPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        var saved = repository.save(UserPersistenceAssembler.toEntity(user));
        return UserPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(UserPersistenceAssembler::toDomain).toList();
    }
}
