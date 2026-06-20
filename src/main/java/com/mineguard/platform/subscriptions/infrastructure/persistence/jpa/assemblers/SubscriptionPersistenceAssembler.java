package com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.assemblers;
import com.mineguard.platform.subscriptions.domain.model.aggregates.Subscription;
import com.mineguard.platform.subscriptions.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
public final class SubscriptionPersistenceAssembler {
    private SubscriptionPersistenceAssembler(){}
    public static Subscription toDomain(SubscriptionPersistenceEntity e){ if(e==null)return null; var s=new Subscription(e.getCompanyId(),e.getPlanId(),e.getStartDate(),e.getEndDate(),e.getStatus()); s.setId(e.getId()); return s; }
    public static SubscriptionPersistenceEntity toEntity(Subscription s){ var e=new SubscriptionPersistenceEntity(); e.setId(s.getId()); e.setCompanyId(s.getCompanyId()); e.setPlanId(s.getPlanId()); e.setStartDate(s.getStartDate()); e.setEndDate(s.getEndDate()); e.setStatus(s.getStatus()); return e; }
}
