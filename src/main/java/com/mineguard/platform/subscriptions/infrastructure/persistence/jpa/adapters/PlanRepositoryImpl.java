package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import com.mineguard.platform.subscriptions.domain.repositories.PlanRepository;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public class PlanRepositoryImpl implements PlanRepository {
    private final PlanPersistenceRepository repository;
    public PlanRepositoryImpl(PlanPersistenceRepository repository) {
        this.repository = repository;
    }
    @Override
    public Plan save(Plan plan) {
        return PlanPersistenceAssembler.toDomain(repository.save(PlanPersistenceAssembler.toEntity(plan)));
    }
    @Override
    public Optional<Plan> findById(Long id) {
        return repository.findById(id).map(PlanPersistenceAssembler::toDomain);
    }
    @Override
    public List<Plan> findAll() {
        return repository.findAll().stream().map(PlanPersistenceAssembler::toDomain).toList();
    }
}
