package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.adapters;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Plan;
import com.mineguard.platform.subscriptions.domain.repositories.PlanRepository;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers.PlanPersistenceAssembler;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.repositories.PlanPersistenceRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class PlanRepositoryImpl implements PlanRepository {
    private final PlanPersistenceRepository r;
    public PlanRepositoryImpl(PlanPersistenceRepository r){this.r=r;}
    public Plan save(Plan p){return PlanPersistenceAssembler.toDomain(r.save(PlanPersistenceAssembler.toEntity(p)));}
    public List<Plan> findAll(){return r.findAll().stream().map(PlanPersistenceAssembler::toDomain).toList();}
    public long count(){return r.count();}
}
