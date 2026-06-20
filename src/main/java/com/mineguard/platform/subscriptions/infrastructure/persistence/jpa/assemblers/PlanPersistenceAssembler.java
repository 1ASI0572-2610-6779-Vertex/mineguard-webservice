package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Plan;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.PlanPersistenceEntity;
public final class PlanPersistenceAssembler {
    private PlanPersistenceAssembler(){}
    public static Plan toDomain(PlanPersistenceEntity e){ if(e==null)return null; var p=new Plan(e.getName(),e.getPrice(),e.getDurationDays()); p.setId(e.getId()); return p; }
    public static PlanPersistenceEntity toEntity(Plan p){ var e=new PlanPersistenceEntity(); e.setId(p.getId()); e.setName(p.getName()); e.setPrice(p.getPrice()); e.setDurationDays(p.getDurationDays()); return e; }
}
