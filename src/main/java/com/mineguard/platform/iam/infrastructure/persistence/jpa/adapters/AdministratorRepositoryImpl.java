package com.mineguard.platform.iam.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.iam.domain.model.aggregates.Administrator;
import com.mineguard.platform.iam.domain.repositories.AdministratorRepository;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers.AdministratorPersistenceAssembler;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories.AdministratorPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdministratorRepositoryImpl implements AdministratorRepository {
    private final AdministratorPersistenceRepository repository;

    public AdministratorRepositoryImpl(AdministratorPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Administrator save(Administrator administrator) {
        return AdministratorPersistenceAssembler.toDomain(repository.save(AdministratorPersistenceAssembler.toEntity(administrator)));
    }

    @Override
    public Optional<Administrator> findById(Long id) {
        return repository.findById(id).map(AdministratorPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public List<Administrator> findAll() {
        return repository.findAll().stream().map(AdministratorPersistenceAssembler::toDomain).toList();
    }
}