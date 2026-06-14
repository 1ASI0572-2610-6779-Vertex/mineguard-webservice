package com.mineguard.platform.iam.infrastructure.persistence.jpa.adapters;

import com.mineguard.platform.iam.domain.model.aggregates.Supervisor;
import com.mineguard.platform.iam.domain.repositories.SupervisorRepository;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.assemblers.SupervisorPersistenceAssembler;
import com.mineguard.platform.iam.infrastructure.persistence.jpa.repositories.SupervisorPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SupervisorRepositoryImpl implements SupervisorRepository {
    private final SupervisorPersistenceRepository repository;

    public SupervisorRepositoryImpl(SupervisorPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public Supervisor save(Supervisor supervisor) {
        var saved = repository.save(SupervisorPersistenceAssembler.toEntity(supervisor));
        return SupervisorPersistenceAssembler.toDomain(saved);
    }

    @Override
    public Optional<Supervisor> findById(Long id) {
        return repository.findById(id).map(SupervisorPersistenceAssembler::toDomain);
    }

    @Override
    public boolean existsByCorporateId(String corporateId) {
        return repository.existsByCorporateId(corporateId);
    }

    @Override
    public List<Supervisor> findAll() {
        return repository.findAll().stream().map(SupervisorPersistenceAssembler::toDomain).toList();
    }
}
