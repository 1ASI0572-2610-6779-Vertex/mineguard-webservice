package com.mineguard.platform.subscriptions.domain.repositories;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Plan;
import java.util.List;
public interface PlanRepository { Plan save(Plan p); List<Plan> findAll(); long count(); }
