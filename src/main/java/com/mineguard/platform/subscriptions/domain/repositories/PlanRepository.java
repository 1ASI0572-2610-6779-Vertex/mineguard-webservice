package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.entities.Plan;
import java.util.List;
import java.util.Optional;
public interface PlanRepository {
    Plan save(Plan plan);
    Optional<Plan> findById(Long id);
    List<Plan> findAll();
}
